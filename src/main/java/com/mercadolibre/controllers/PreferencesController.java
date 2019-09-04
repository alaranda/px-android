package com.mercadolibre.controllers;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.dto.preference.PreferenceResponse;
import com.mercadolibre.dto.public_key.PublicKeyInfo;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.service.AuthService;
import com.mercadolibre.service.PreferenceService;
import com.mercadolibre.utils.ErrorsConstants;
import com.mercadolibre.utils.Locale;
import com.mercadolibre.utils.datadog.DatadogPreferencesMetric;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import static com.mercadolibre.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.px.toolkit.utils.logs.LogBuilder.requestInLogBuilder;

public enum PreferencesController {
    INSTANCE;

    private static final String CONTROLLER_NAME = "InitPreferenceController";
    private static final Logger logger = LogManager.getLogger();

    /**
     * Devuelve una publick key y una pref.
     *
     * @param request  request
     * @param response response
     * @return payment
     * @throws ApiException   si falla el api call (status code is not 2xx)
     */
    public PreferenceResponse initCheckoutByPref(final Request request, final Response response) throws ApiException, ExecutionException, InterruptedException, MalformedURLException {

        final Context context = new Context.Builder(request.attribute(REQUEST_ID)).locale(Locale.getLocale(request)).build();
        try {
            final long callerId = Long.valueOf(request.queryParams(Constants.CALLER_ID_PARAM));
            final long clientId = Long.valueOf(request.queryParams(Constants.CLIENT_ID_PARAM));
            final String prefId = PreferenceService.INSTANCE.extractParamPrefId(context, request);

            final Preference preference = PreferenceService.INSTANCE.getPreference(context, prefId, callerId);
            final PublicKeyInfo publicKey = AuthService.INSTANCE.getPublicKey(context, preference.getCollectorId().toString(), PreferenceService.INSTANCE.getClientId(preference.getClientId(), clientId));
            final PreferenceResponse preferenceResponse = new PreferenceResponse(prefId, publicKey.getPublicKey());

            DatadogPreferencesMetric.addPreferenceData(preference);
            logInitPref(context, preferenceResponse, preference);

            return preferenceResponse;
        } catch (ApiException e) {
            throw new ApiException(e.getCode(), ErrorsConstants.getGeneralError(context.getLocale()), e.getStatusCode());
        }
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
