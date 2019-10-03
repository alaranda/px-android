package com.mercadolibre.router;

import com.google.common.net.MediaType;
import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.constants.HeadersConstants;
import com.mercadolibre.controllers.CongratsController;
import com.mercadolibre.controllers.PaymentsController;
import com.mercadolibre.controllers.PreferencesController;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.utils.logs.LogBuilder;
import com.mercadolibre.utils.datadog.DatadogRequestMetric;
import com.mercadolibre.utils.newRelic.NewRelicUtils;
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

import static com.mercadolibre.constants.HeadersConstants.REQUEST_ID;
import static spark.Spark.afterAfter;

public class Router implements SparkApplication {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String REQUEST_START_HEADER = "request-start";
    private static final String CONTENT_ENCODING_GZIP = "gzip";

    private static final String INTERNAL_ERROR = "internal error";

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

            Spark.get("/congrats", new MeteredRoute(CongratsController.INSTANCE::getCongrats,
                    "/congrats"), GsonWrapper::toJson);

            Spark.exception(ApiException.class, (exception, request, response) -> {

                LOGGER.error(LogBuilder.requestOutLogBuilder(request.attribute(REQUEST_ID))
                        .withStatus(exception.getStatusCode())
                        .withExceptionCodeAndDescription(exception.getCode(), exception.getDescription())
                        .build()
                );
                NewRelicUtils.noticeError(exception, request);

                ApiError apiError = new ApiError(exception.getMessage(),
                        exception.getDescription(), exception.getStatusCode());

                response.status(exception.getStatusCode());
                response.type(MediaType.JSON_UTF_8.toString());
                response.body(GsonWrapper.toJson(apiError));
            });

            Spark.exception(Exception.class, (exception, request, response) -> {

                LOGGER.error(LogBuilder.requestInLogBuilder(request.attribute(REQUEST_ID))
                        .withStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                        .withMessage("Exception thrown")
                        .build(), exception);
                NewRelicUtils.noticeError(exception, request);

                final String apiErrorJson = GsonWrapper.toJson(new ApiError(INTERNAL_ERROR,
                        "internal_error", HttpStatus.SC_INTERNAL_SERVER_ERROR));
                response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                response.type(MediaType.JSON_UTF_8.toString());
                response.body(apiErrorJson);

            });

            Spark.exception(ValidationException.class, (exception, request, response) -> {

                LOGGER.error(LogBuilder.requestInLogBuilder(request.attribute(REQUEST_ID))
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withMessage(String.format("Validation exception produced, message[%s]", exception.getMessage()))
                        .build());
                NewRelicUtils.noticeError(exception, request);

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
            LOGGER.debug(LogBuilder.requestInLogBuilder(requestId).withMessage("Start new request ID: " + requestId).build());
        }

        request.attribute(Constants.REQUEST_ID, requestId);
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
            response.header(HttpHeaders.CACHE_CONTROL, HeadersConstants.NO_CACHE_PARAMS);
        }
    }

}
