package com.mercadolibre.router;

import static com.mercadolibre.px.constants.ErrorCodes.INTERNAL_ERROR;
import static com.mercadolibre.px.constants.HeadersConstants.*;
import static com.mercadolibre.px.monitoring.lib.log.LogBuilder.requestInLogBuilder;
import static com.mercadolibre.px.monitoring.lib.log.LogBuilder.requestOutLogBuilder;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static spark.Spark.afterAfter;

import com.google.common.net.MediaType;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.controllers.*;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.exceptions.ValidationException;
import com.mercadolibre.px.monitoring.lib.new_relic.NewRelicUtils;
import com.mercadolibre.px.monitoring.lib.new_relic.dto.NewRelicRequest;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.px_config.ApiContext;
import com.mercadolibre.px_config.Config;
import com.mercadolibre.utils.datadog.DatadogRequestMetric;
import com.newrelic.api.agent.NewRelic;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.servlet.SparkApplication;

public class Router implements SparkApplication {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String REQUEST_START_HEADER = "request-start";
  private static final String CONTENT_ENCODING_GZIP = "gzip";

  private static final String V1_CHA_URL = "/authentication/v1/card_holder";

  private final CongratsController congratsController = new CongratsController();
  private final CapEscController capEscController = new CapEscController();
  private final RemediesController remediesController = new RemediesController();
  private final AuthenticationController authenticationController = new AuthenticationController();

  @Override
  public void init() {

    setupFilters();

    Spark.get(
        "/ping",
        (req, res) -> {
          NewRelic.ignoreTransaction();
          res.status(HttpServletResponse.SC_OK);
          res.type(MediaType.PLAIN_TEXT_UTF_8.toString());
          return "pong";
        });

    Spark.path(
        "/px_mobile",
        () -> {
          Spark.post(
              "/legacy_payments",
              new MeteredRoute(PaymentsController.INSTANCE::doLegacyPayment, "/legacy_payments"),
              GsonWrapper::toJson);

          Spark.post(
              "/payments",
              new MeteredRoute(PaymentsController.INSTANCE::doPayment, "/payments"),
              GsonWrapper::toJson);

          Spark.get(
              "/init/preference",
              new MeteredRoute(
                  PreferencesController.INSTANCE::initCheckoutByPref, "/init/preference"),
              GsonWrapper::toJson);

          Spark.get(
              "/congrats",
              new MeteredRoute(congratsController::getCongrats, "/congrats"),
              GsonWrapper::toJson);

          Spark.delete(
              "/v1/esc_cap/:cardId",
              new MeteredRoute(capEscController::resetCapEsc, "/v1/esc_cap/:cardId"),
              GsonWrapper::toJson);

          Spark.post(
              "/v1/remedies/:paymentId",
              new MeteredRoute(remediesController::getRemedy, "/v1/remedies/:paymentId"),
              GsonWrapper::toJson);

          Spark.post(
              V1_CHA_URL,
              new MeteredRoute(authenticationController::authenticateCardHolder, V1_CHA_URL),
              GsonWrapper::toJson);

          Spark.exception(
              ApiException.class,
              (exception, request, response) -> {
                LOGGER.error(
                    requestOutLogBuilder(request.attribute(X_REQUEST_ID))
                        .withStatus(exception.getStatusCode())
                        .withException(exception.getCode(), exception.getDescription())
                        .build());
                NewRelicRequest NRRequest =
                    NewRelicRequest.builder()
                        .withRequestId(request.attribute(X_REQUEST_ID))
                        .build();
                NewRelicUtils.noticeError(exception, NRRequest);

                ApiError apiError =
                    new ApiError(
                        exception.getMessage(),
                        exception.getDescription(),
                        exception.getStatusCode());

                response.status(exception.getStatusCode());
                response.type(MediaType.JSON_UTF_8.toString());
                response.body(GsonWrapper.toJson(apiError));
              });

          Spark.exception(
              Exception.class,
              (exception, request, response) -> {
                LOGGER.error(
                    requestInLogBuilder(request.attribute(X_REQUEST_ID))
                        .withStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                        .withMessage("Exception thrown")
                        .build(),
                    exception);
                NewRelicRequest NRRequest =
                    NewRelicRequest.builder()
                        .withRequestId(request.attribute(X_REQUEST_ID))
                        .build();
                NewRelicUtils.noticeError(exception, NRRequest);

                final String apiErrorJson =
                    GsonWrapper.toJson(
                        new ApiError(
                            INTERNAL_ERROR, INTERNAL_ERROR, HttpStatus.SC_INTERNAL_SERVER_ERROR));
                response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                response.type(MediaType.JSON_UTF_8.toString());
                response.body(apiErrorJson);
              });

          Spark.exception(
              ValidationException.class,
              (exception, request, response) -> {
                LOGGER.error(
                    requestInLogBuilder(request.attribute(X_REQUEST_ID))
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withMessage(
                            String.format(
                                "Validation exception produced, message[%s]",
                                exception.getDescription()))
                        .build());
                NewRelicRequest NRRequest =
                    NewRelicRequest.builder()
                        .withRequestId(request.attribute(X_REQUEST_ID))
                        .build();
                NewRelicUtils.noticeError(exception, NRRequest);

                ApiError apiError =
                    new ApiError(exception.getMessage(), "bad request", HttpStatus.SC_BAD_REQUEST);
                response.status(HttpStatus.SC_BAD_REQUEST);
                response.type(MediaType.JSON_UTF_8.toString());
                response.body(GsonWrapper.toJson(apiError));
              });

          Spark.after(
              (req, res) -> {
                res.header(HttpHeaders.CONTENT_ENCODING, CONTENT_ENCODING_GZIP);
              });
        });

    afterAfter(DatadogRequestMetric::incrementRequestCounter);
  }

  private static void setRequestId(final Request request) {
    request.attribute(Constants.API_CONTEXT, ApiContext.getApiContextFromScope(Config.getSCOPE()));

    String requestId = request.headers(X_REQUEST_ID);
    if (isBlank(requestId)) {
      requestId = UUID.randomUUID().toString();
      LOGGER.debug(
          requestInLogBuilder(requestId).withMessage("Start new request ID: " + requestId).build());
    }

    request.attribute(X_REQUEST_ID, requestId);
  }

  private void setupFilters() {
    Spark.before("/px_mobile/*", (request, response) -> setRequestId(request));
    Spark.before(
        (request, response) -> request.attribute(REQUEST_START_HEADER, System.currentTimeMillis()));
    Spark.after(Router::setHeaders);
  }

  private static void setHeaders(final Request request, final Response response) {
    if (response.type() == null) {
      response.type(MediaType.JSON_UTF_8.toString());
    }

    response.header(HttpHeaders.VARY, "Accept,Accept-Encoding");

    if (response.raw().getHeader(HttpHeaders.CACHE_CONTROL) == null) {
      response.header(HttpHeaders.CACHE_CONTROL, NO_CACHE_PARAMS);
    }
  }
}
