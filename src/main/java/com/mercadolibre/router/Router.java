package com.mercadolibre.router;

import com.google.common.net.MediaType;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.controllers.CapEscController;
import com.mercadolibre.controllers.CongratsController;
import com.mercadolibre.controllers.PaymentsController;
import com.mercadolibre.controllers.PreferencesController;
import com.mercadolibre.px.toolkit.config.Config;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.dto.NewRelicRequest;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.new_relic.NewRelicUtils;
import com.mercadolibre.px.toolkit.utils.ApiContext;
import com.mercadolibre.utils.datadog.DatadogRequestMetric;
import com.newrelic.api.agent.NewRelic;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.servlet.SparkApplication;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.REQUEST_ID;
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.INTERNAL_ERROR;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.NO_CACHE_PARAMS;
import static com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder.requestInLogBuilder;
import static com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder.requestOutLogBuilder;
import static spark.Spark.afterAfter;

public class Router implements SparkApplication {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String REQUEST_START_HEADER = "request-start";
    private static final String CONTENT_ENCODING_GZIP = "gzip";

    private final CongratsController congratsController = new CongratsController();
    private final CapEscController capEscController = new CapEscController();

    @Override
    public void init() {

        setupFilters();

        Spark.get("/ping", (req, res) -> {
            NewRelic.ignoreTransaction();
            res.status(HttpServletResponse.SC_OK);
            res.type(MediaType.PLAIN_TEXT_UTF_8.toString());
            return "pong";
        });

        Spark.path("/px_mobile", () -> {

            Spark.post("/legacy_payments", new MeteredRoute(PaymentsController.INSTANCE::doLegacyPayment,
                    "/legacy_payments"), GsonWrapper::toJson);

            Spark.post("/payments", new MeteredRoute(PaymentsController.INSTANCE::doPayment,
                    "/payments"), GsonWrapper::toJson);


            Spark.get("/init/preference", new MeteredRoute(PreferencesController.INSTANCE::initCheckoutByPref,
                    "/init/preference"), GsonWrapper::toJson);

            Spark.get("/congrats", new MeteredRoute(congratsController::getCongrats,
                    "/congrats"), GsonWrapper::toJson);

            Spark.delete("/v1/esc_cap/:cardId", new MeteredRoute(capEscController::resetCapEsc,
                    "/v1/esc_cap/:cardId"), GsonWrapper::toJson);

            Spark.exception(ApiException.class, (exception, request, response) -> {

                LOGGER.error(requestOutLogBuilder(request.attribute(REQUEST_ID))
                        .withStatus(exception.getStatusCode())
                        .withException(exception.getCode(), exception.getDescription())
                        .build()
                );
                NewRelicRequest NRRequest = NewRelicRequest.builder()
                        .withRequestId(request.attribute(REQUEST_ID))
                        .build();
                NewRelicUtils.noticeError(exception, NRRequest);

                ApiError apiError = new ApiError(exception.getMessage(),
                        exception.getDescription(), exception.getStatusCode());

                response.status(exception.getStatusCode());
                response.type(MediaType.JSON_UTF_8.toString());
                response.body(GsonWrapper.toJson(apiError));
            });

            Spark.exception(Exception.class, (exception, request, response) -> {

                LOGGER.error(requestInLogBuilder(request.attribute(REQUEST_ID))
                        .withStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                        .withMessage("Exception thrown")
                        .build(), exception);
                NewRelicRequest NRRequest = NewRelicRequest.builder()
                        .withRequestId(request.attribute(REQUEST_ID))
                        .build();
                NewRelicUtils.noticeError(exception, NRRequest);

                final String apiErrorJson = GsonWrapper.toJson(new ApiError(INTERNAL_ERROR,
                        INTERNAL_ERROR, HttpStatus.SC_INTERNAL_SERVER_ERROR));
                response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                response.type(MediaType.JSON_UTF_8.toString());
                response.body(apiErrorJson);

            });

            Spark.exception(ValidationException.class, (exception, request, response) -> {

                LOGGER.error(requestInLogBuilder(request.attribute(REQUEST_ID))
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withMessage(String.format("Validation exception produced, message[%s]", exception.getMessage()))
                        .build());
                NewRelicRequest NRRequest = NewRelicRequest.builder()
                        .withRequestId(request.attribute(REQUEST_ID))
                        .build();
                NewRelicUtils.noticeError(exception, NRRequest);

                ApiError apiError = new ApiError(exception.getMessage(), "bad request", HttpStatus.SC_BAD_REQUEST);
                response.status(HttpStatus.SC_BAD_REQUEST);
                response.type(MediaType.JSON_UTF_8.toString());
                response.body(GsonWrapper.toJson(apiError));
            });

            Spark.after((req, res) -> {
                res.header(HttpHeaders.CONTENT_ENCODING, CONTENT_ENCODING_GZIP);
            });
        });

        afterAfter(DatadogRequestMetric::incrementRequestCounter);
    }

    private static void setRequestIdAndLogRequest(final Request request) {
        request.attribute(Constants.API_CONTEXT, ApiContext.getApiContextFromScope(Config.getSCOPE()));

        String requestId = request.headers(REQUEST_ID);
        if (StringUtils.isBlank(requestId)) {
            requestId = UUID.randomUUID().toString();
            LOGGER.debug(requestInLogBuilder(requestId).withMessage("Start new request ID: " + requestId).build());
        }

        request.attribute(REQUEST_ID, requestId);
    }

    private void setupFilters() {
        Spark.before("/px_mobile/*", (request, response) -> setRequestIdAndLogRequest(request));
        Spark.before((request, response) ->request.attribute(REQUEST_START_HEADER, System.currentTimeMillis()));
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
