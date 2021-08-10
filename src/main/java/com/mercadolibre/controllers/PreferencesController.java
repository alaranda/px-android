package com.mercadolibre.controllers;

import static com.mercadolibre.constants.Constants.*;
import static com.mercadolibre.px.constants.CommonParametersNames.*;
import static com.mercadolibre.px.constants.HeadersConstants.SESSION_ID;
import static com.mercadolibre.utils.Translations.PAYMENT_NOT_PROCESSED;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.preference.InitPreferenceRequest;
import com.mercadolibre.dto.preference.PreferenceResponse;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.exceptions.ValidationException;
import com.mercadolibre.px.monitoring.lib.log.LogBuilder;
import com.mercadolibre.px.monitoring.lib.utils.LogUtils;
import com.mercadolibre.service.PreferenceService;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.assemblers.ContextAssembler;
import com.mercadolibre.utils.datadog.DatadogPreferencesMetric;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

public enum PreferencesController {
  INSTANCE;

  private static final String CONTROLLER_NAME = "PreferencesController";
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Devuelve una public key y una pref.
   *
   * @param request request
   * @param response response
   * @return preferenceResponse response pref
   * @throws ExecutionException execution exception
   * @throws ApiException api exception
   * @throws InterruptedException interrupted exception
   */
  public PreferenceResponse initCheckoutByPref(final Request request, final Response response)
      throws ApiException, ExecutionException, InterruptedException {

    final Context context = ContextAssembler.toContext(request);

    LogBuilder logBuilder =
        new LogBuilder(context.getRequestId(), LogBuilder.REQUEST_IN)
            .withSource(CONTROLLER_NAME)
            .withMethod(request.requestMethod())
            .withUrl(request.url())
            .withUserAgent(request.userAgent())
            .withAcceptLanguage(context.getLocale().toString());
    Optional.ofNullable(request.headers(SESSION_ID)).ifPresent(logBuilder::withSessionId);
    LogUtils.getQueryParams(request.queryString()).ifPresent(logBuilder::withParams);
    LOGGER.info(logBuilder.build());

    try {

      final InitPreferenceRequest initPreferenceRequest = getInitPreferenceRequest(request);
      final PreferenceResponse preferenceResponse =
          PreferenceService.INSTANCE.getPreferenceResponse(context, initPreferenceRequest);

      DatadogPreferencesMetric.addPreferenceData(context, preferenceResponse);
      return preferenceResponse;

    } catch (ApiException e) {
      throw new ApiException(
          e.getCode(),
          Translations.INSTANCE.getTranslationByLocale(context.getLocale(), PAYMENT_NOT_PROCESSED),
          e.getStatusCode());
    }
  }

  private InitPreferenceRequest getInitPreferenceRequest(final Request request)
      throws ApiException {

    final String callerId = request.queryParams(CALLER_ID);
    final String clientId = request.queryParams(CLIENT_ID);
    final String shortId = request.queryParams(Constants.SHORT_ID);
    final String prefId = request.queryParams(Constants.PREF_ID);
    final String flowId = request.queryParams(Constants.FLOW_ID);

    if (null == callerId) {
      throw new ValidationException("caller id required");
    }
    if (null == clientId) {
      throw new ValidationException("client id required");
    }

    if (StringUtils.isBlank(shortId) && StringUtils.isBlank(prefId)) {
      throw new ApiException(INVALID_PARAMS, GETTING_PARAMETERS, HttpStatus.SC_BAD_REQUEST);
    }

    return new InitPreferenceRequest(callerId, clientId, prefId, shortId, flowId);
  }
}
