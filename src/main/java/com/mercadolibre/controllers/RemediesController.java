package com.mercadolibre.controllers;

import static com.mercadolibre.constants.Constants.PAYMENT_ID;
import static com.mercadolibre.constants.QueryParamsConstants.FLOW_NAME;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_SITE_ID;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.LANGUAGE;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.PLATFORM;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.SESSION_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder.REQUEST_IN;
import static com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder.requestInLogBuilder;
import static com.mercadolibre.utils.HeadersUtils.ONE_TAP;

import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.toolkit.constants.CommonParametersNames;
import com.mercadolibre.px.toolkit.constants.HeadersConstants;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder;
import com.mercadolibre.service.RemediesService;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

public class RemediesController {

  private static final Logger LOGGER = LogManager.getLogger();

  private final RemediesService remediesService;
  private static final String CONTROLLER_NAME = "RemediesController";

  public RemediesController() {
    this.remediesService = new RemediesService();
  }

  /**
   * Recibe un payment id, y en base a eso se fija el status detail e intenta recueprar el pago
   * faliido.
   *
   * @param request request
   * @param response response
   * @return void
   */
  public RemediesResponse getRemedy(final Request request, final Response response)
      throws ApiException {

    final Context.ContextBuilder contextBuilder =
        Context.builder()
            .requestId(request.attribute(CommonParametersNames.REQUEST_ID))
            .locale(request.headers(LANGUAGE))
            .flow(request.queryParams(FLOW_NAME));

    if (StringUtils.isNotBlank(request.headers(PLATFORM))) {
      final Platform platform = Platform.from(request.headers(PLATFORM));
      contextBuilder.platform(platform);
    }

    final Context context = contextBuilder.build();

    LOGGER.info(
        new LogBuilder(request.attribute(HeadersConstants.REQUEST_ID), REQUEST_IN)
            .withSource(CONTROLLER_NAME)
            .withMethod(request.requestMethod())
            .withUrl(request.url())
            .withUserAgent(request.userAgent())
            .withSessionId(request.headers(SESSION_ID))
            .withParams(request.queryParams().toString()));

    final String paymentId = request.params(PAYMENT_ID);

    validateParams(paymentId);

    final RemediesRequest remediesRequest = getRemedyRequest(request);

    final RemediesResponse remediesResponse =
        remediesService.getRemedy(context, paymentId, remediesRequest);

    logRemedies(context, remediesResponse);

    return remediesResponse;
  }

  private RemediesRequest getRemedyRequest(final Request request) throws ApiException {
    try {
      final RemediesRequest remediesRequest =
          GsonWrapper.fromJson(request.body(), RemediesRequest.class);
      remediesRequest.setSiteId(request.queryParams(CALLER_SITE_ID));
      remediesRequest.setUserAgent(UserAgent.create(request.userAgent()));
      remediesRequest.setUserId(request.queryParams(CALLER_ID));
      remediesRequest.setOneTap(
          StringUtils.isBlank(request.headers(ONE_TAP))
              ? true
              : Boolean.parseBoolean(request.headers(ONE_TAP)));
      return remediesRequest;
    } catch (Exception e) {
      throw new ApiException("Bad Request", "Error parsing body", HttpStatus.SC_BAD_REQUEST);
    }
  }

  private void validateParams(final String paymentId) throws ValidationException {

    if (StringUtils.isBlank(paymentId)) {
      final ValidationException validationException =
          new ValidationException("payment id required");
      LOGGER.error(validationException);
      throw validationException;
    }
  }

  private void logRemedies(final Context context, final RemediesResponse remediesResponse) {
    LOGGER.info(
        requestInLogBuilder(context.getRequestId())
            .withSource(RemediesController.class.getSimpleName())
            .withStatus(HttpStatus.SC_OK)
            .withResponse(remediesResponse.toLog(remediesResponse))
            .build());
  }
}
