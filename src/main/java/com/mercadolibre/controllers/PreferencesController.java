package com.mercadolibre.controllers;

import com.mercadolibre.dto.preference.PreferenceResponse;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.user.PublicKey;
import com.mercadolibre.px.toolkit.constants.HeadersConstants;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder;
import com.mercadolibre.service.AuthService;
import com.mercadolibre.service.PreferenceService;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.datadog.DatadogPreferencesMetric;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

import java.util.concurrent.ExecutionException;

import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CLIENT_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.REQUEST_ID;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.LANGUAGE;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.SESSION_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder.REQUEST_IN;
import static com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder.requestInLogBuilder;
import static com.mercadolibre.utils.Translations.PAYMENT_NOT_PROCESSED;

public enum PreferencesController {
    INSTANCE;

    private static final String CONTROLLER_NAME = "InitPreferenceController";
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Devuelve una publick key y una pref.
     *
     * @param request  request
     * @param response response
     * @return preferenceResponse response pref
     * @throws ExecutionException execution exception
     * @throws ApiException        api exception
     * @throws InterruptedException  interrupted exception
     */
    public PreferenceResponse initCheckoutByPref(final Request request, final Response response) throws ApiException, ExecutionException, InterruptedException {

        final Context context = Context.builder()
                .requestId(request.attribute(REQUEST_ID))
                .locale(request.headers(LANGUAGE))
                .build();
        LOGGER.info(
                new LogBuilder(request.attribute(HeadersConstants.REQUEST_ID), REQUEST_IN)
                        .withSource(CONTROLLER_NAME)
                        .withMethod(request.requestMethod())
                        .withUrl(request.url())
                        .withUserAgent(request.userAgent())
                        .withSessionId(request.headers(SESSION_ID))
                        .withAcceptLanguage(context.getLocale().toString())
                        .withParams(request.queryString())
                        .build()
        );

        try {

            final String callerIdQuery = request.queryParams(CALLER_ID);
            if (null == callerIdQuery) { throw new ValidationException("caller id required"); }
            final Long callerId = Long.valueOf(callerIdQuery);

            final String clientIdQuery = request.queryParams(CLIENT_ID);
            if (null == clientIdQuery) { throw new ValidationException("client id required"); }
            final Long clientId = Long.valueOf(clientIdQuery);

            final String prefId = PreferenceService.INSTANCE.extractParamPrefId(context, request);

            final Preference preference = PreferenceService.INSTANCE.getPreference(context, prefId, callerId);
            final PublicKey publicKey = AuthService.INSTANCE.getPublicKey(context, preference.getCollectorId(), PreferenceService.INSTANCE.getClientId(preference.getClientId(), clientId));
            final PreferenceResponse preferenceResponse = new PreferenceResponse(prefId, publicKey.getPublicKey());

            DatadogPreferencesMetric.addPreferenceData(preference);
            logInitPref(context, preferenceResponse, preference);

            return preferenceResponse;
        } catch (ApiException e) {
            throw new ApiException(e.getCode(), Translations.INSTANCE.getTranslationByLocale(context.getLocale(), PAYMENT_NOT_PROCESSED), e.getStatusCode());
        }
    }

    private void logInitPref(final Context context, final PreferenceResponse preferenceResponse, final Preference preference) {
        LOGGER.info(requestInLogBuilder(context.getRequestId())
                .withSource(CONTROLLER_NAME)
                .withStatus(HttpStatus.SC_OK)
                .withClientId(String.valueOf(preference.getClientId()))
                .withMessage(preferenceResponse.toLog(preferenceResponse))
                .build());
    }
}
