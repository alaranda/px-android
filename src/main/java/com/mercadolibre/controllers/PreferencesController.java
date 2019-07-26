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
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.service.AuthService;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.ErrorsConstants;
import com.mercadolibre.utils.datadog.DatadogPreferencesMetric;
import com.mercadolibre.validators.PreferencesValidator;
import com.newrelic.api.agent.Trace;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mercadolibre.constants.HeadersConstants.LANGUAGE;
import static com.mercadolibre.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.px.toolkit.utils.logs.LogBuilder.requestInLogBuilder;

public enum PreferencesController {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger();
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
            final Context context = new Context(new Context.Builder(request.attribute(REQUEST_ID)));
            final long clientId = Long.valueOf(request.queryParams(Constants.CLIENT_ID_PARAM));
            final String prefId = extractParamPrefId(request, context);

            final CompletableFuture<Either<Preference, ApiError>> futurePreference =
                    PreferenceAPI.INSTANCE.geAsynctPreference(context, prefId);

            CompletableFuture.allOf(futurePreference);

            if (!futurePreference.get().isValuePresent()) {
                final ApiError apiError = futurePreference.get().getAlternative();
                throw new ApiException(ErrorsConstants.EXTERNAL_ERROR, ErrorsConstants.API_CALL_FAILED, apiError.getStatus());
            }

            final Preference preference = futurePreference.get().getValue();
            validatePref(preference);

            final PublicKeyInfo publicKey = AuthService.INSTANCE.getPublicKey(context, preference.getCollectorId().toString(),
                   clientId);

            final PreferenceResponse preferenceResponse = new PreferenceResponse(prefId, publicKey.getPublicKey());
            DatadogPreferencesMetric.addPreferenceData(preference);
            logInitPref(context, preferenceResponse, preference);
            return preferenceResponse;
        } catch (ApiException e) {
            throw new ApiException(e.getCode(), ErrorsConstants.getGeneralErrorByLanguage(request.headers(LANGUAGE)), e.getStatusCode());
        }
    }

    private String extractParamPrefId(final Request request, final Context context) throws ApiException {

        if (!StringUtils.isBlank(request.queryParams(Constants.SHORT_ID))){

            return isShortKey(context, request.queryParams(Constants.SHORT_ID));

        } else if (request.queryParams(Constants.PREF_ID) != null){

            return request.queryParams(Constants.PREF_ID);
        }

        throw new ApiException(ErrorsConstants.INVALID_PARAMS, ErrorsConstants.GETTING_PARAMETERS, HttpStatus.SC_BAD_REQUEST);
    }

    private String isShortKey(final Context context, final String shortKey) throws ApiException {

        try {
            final PreferenceTidy preferenceTidy = PreferenceTidyApi.INSTANCE.getPreferenceByKey(context, shortKey);
            String[] splitLongUrl = preferenceTidy.getLongUrl().split("=");
            if (splitLongUrl.length != 2) {
                throw new ApiException(ErrorsConstants.EXTERNAL_ERROR, ErrorsConstants.INVALID_PREFERENCE, HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            String preId = splitLongUrl[1];
            return preId;

        } catch (Exception e) {
            throw new ApiException(e.getMessage(), ErrorsConstants.GETTING_PARAMETERS, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void validatePref(final Preference preference) throws ValidationException {
        final PreferencesValidator validator = new PreferencesValidator();
        validator.validate(preference);
    }

    private void logInitPref(final Context context, final PreferenceResponse preferenceResponse, final Preference preference) {

        logger.info(requestInLogBuilder(context.getRequestId())
                .withSource(CONTROLLER_NAME)
                .withStatus(HttpStatus.SC_OK)
                .withClientId(String.valueOf(preference.getClientId()))
                .withMessage(preferenceResponse.toLog(preferenceResponse))
                .build());
    }
}
