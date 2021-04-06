package com.mercadolibre.controllers;

import static com.mercadolibre.constants.Constants.PAYMENT_ID;
import static com.mercadolibre.px.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.constants.CommonParametersNames.CALLER_SITE_ID;
import static com.mercadolibre.px.constants.HeadersConstants.SESSION_ID;
import static com.mercadolibre.px.monitoring.lib.log.LogBuilder.requestInLogBuilder;
import static com.mercadolibre.utils.HeadersUtils.ONE_TAP;

import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.px.constants.HeadersConstants;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.exceptions.ValidationException;
import com.mercadolibre.px.monitoring.lib.log.LogBuilder;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.service.RemediesService;
import com.mercadolibre.utils.assemblers.ContextAssembler;
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

  private static final String NULL_VALUE = "null";

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
   * @throws ApiException API Exception
   */
  public RemediesResponse getRemedy(final Request request, final Response response)
      throws ApiException {

    final Context context = ContextAssembler.toContext(request);

    LOGGER.info(
        new LogBuilder(request.attribute(HeadersConstants.X_REQUEST_ID), LogBuilder.REQUEST_IN)
            .withSource(CONTROLLER_NAME)
            .withMethod(request.requestMethod())
            .withUrl(request.url())
            .withUserAgent(request.userAgent())
            .withSessionId(request.headers(SESSION_ID))
            .withParams(request.queryParams().toString())
            .withFlow(context.getFlow()));

    final String paymentId = request.params(PAYMENT_ID);

    validateParams(paymentId);

    final RemediesRequest remediesRequest = getRemedyRequest(request);

    final RemediesResponse remediesResponse =
        remediesService.getRemedy(context, paymentId, remediesRequest);

    logRemedies(context, remediesResponse);

    return remediesResponse;
  }

  private RemediesRequest getRemedyRequest(final Request request) throws ApiException {

    final RemediesRequest remediesRequest;

    try {
      remediesRequest = GsonWrapper.fromJson(request.body(), RemediesRequest.class);
      remediesRequest.setSiteId(request.queryParams(CALLER_SITE_ID));
      remediesRequest.setUserId(request.queryParams(CALLER_ID));
      remediesRequest.setOneTap(
          StringUtils.isBlank(request.headers(ONE_TAP))
              || Boolean.parseBoolean(request.headers(ONE_TAP)));

    } catch (Exception e) {
      throw new ApiException("Bad Request", "Error parsing body", HttpStatus.SC_BAD_REQUEST);
    }

    return remediesRequest;
  }

  private void validateParams(final String paymentId) throws ValidationException {

    if (StringUtils.isBlank(paymentId) || NULL_VALUE.equalsIgnoreCase(paymentId)) {
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
