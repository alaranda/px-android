package com.mercadolibre.controllers;

import com.mercadolibre.api.PreferenceAPI;
import com.mercadolibre.api.PreferenceTidyApi;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.dto.preference.PreferenceResponse;
import com.mercadolibre.dto.preference.PreferenceTidy;
import com.mercadolibre.dto.public_key.PublicKeyInfo;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.service.AuthService;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.datadog.DatadogPreferencesMetric;
import com.mercadolibre.utils.logs.LogBuilder;
import com.mercadolibre.validators.PreferencesValidator;
import com.newrelic.api.agent.Trace;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mercadolibre.constants.HeadersConstants.REQUEST_ID;

public enum PreferencesController {

    INSTANCE;

    private static final Logger LOG = Logger.getLogger(PreferencesController.class);
    private static final String CONTROLLER_NAME = "InitPreferenceController";

    /**
     * Devuelve una publick key y una pref.
     *
     * @param request  request
     * @param response response
     * @return payment
     * @throws ApiException   si falla el api call (status code is not 2xx)
     */
    @Trace
    public PreferenceResponse initCheckoutByPref(final Request request, final Response response) throws ApiException, ExecutionException, InterruptedException, MalformedURLException {

        try {
            final String requestId = request.attribute(REQUEST_ID);
            final long clientId = Long.valueOf(request.queryParams(Constants.CLIENT_ID_PARAM));
            final String prefId = extractParamPrefId(request, requestId);

            final CompletableFuture<Either<Preference, ApiError>> futurePreference =
                    PreferenceAPI.INSTANCE.geAsynctPreference(prefId, requestId);

            CompletableFuture.allOf(futurePreference);

            if (!futurePreference.get().isValuePresent()) {
                final ApiError apiError = futurePreference.get().getAlternative();
                throw new ApiException("external_error", "API call to preference failed", apiError.getStatus());
            }

            final Preference preference = futurePreference.get().getValue();
            validatePref(preference);

            final PublicKeyInfo publicKey = AuthService.INSTANCE.getPublicKey(requestId, preference.getCollectorId().toString(),
                   clientId);

            final PreferenceResponse preferenceResponse = new PreferenceResponse(prefId, publicKey.getPublicKey());
            DatadogPreferencesMetric.addPreferenceData(preference);
            logInitPref(preferenceResponse, preference);
            return preferenceResponse;
        } catch (ApiException e) {
            throw new ApiException(e.getCode(), "En este momento no estamos pudiendo procesar pagos", e.getStatusCode());
        }
    }

    private String extractParamPrefId(final Request request, final String requestId) throws ApiException {

        if (!StringUtils.isBlank(request.queryParams(Constants.SHORT_ID))){

            return isShortKey(request.queryParams(Constants.SHORT_ID), requestId);

        } else if (request.queryParams(Constants.PREF_ID) != null){

            return request.queryParams(Constants.PREF_ID);
        }

        throw new ApiException("Invalid Params", "Error getting parameters", HttpStatus.BAD_REQUEST_400);
    }

    private String isShortKey(final String shortKey, final String requestId) throws ApiException {

        try {
            final PreferenceTidy preferenceTidy = PreferenceTidyApi.INSTANCE.getPreferenceByKey(requestId, shortKey);
            String[] splitLongUrl = preferenceTidy.getLongUrl().split("=");
            if (splitLongUrl.length != 2) {
                throw new ApiException("external_error", "invalid preference", HttpStatus.INTERNAL_SERVER_ERROR_500);
            }
            String preId = splitLongUrl[1];
            return preId;

        } catch (Exception e) {
            throw new ApiException(e.getMessage(), "Error getting parameters by short key", HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }

    private void validatePref(final Preference preference) throws ValidationException {
        final PreferencesValidator validator = new PreferencesValidator();
        validator.validate(preference);
    }

    private void logInitPref(final PreferenceResponse preferenceResponse, final Preference preference) {
        final LogBuilder logBuilder = new LogBuilder(LogBuilder.LEVEL_INFO, LogBuilder.REQUEST_IN)
                .withSource(CONTROLLER_NAME)
                .withStatus(org.apache.http.HttpStatus.SC_OK)
                .withClientId(String.valueOf(preference.getClientId()))
                .withMessage(preferenceResponse.toLog(preferenceResponse));

        LOG.info(logBuilder.build());
    }
}
