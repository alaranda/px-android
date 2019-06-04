package com.mercadolibre.router;

import com.google.common.net.MediaType;
import com.mercadolibre.config.Config;
import com.mercadolibre.constants.HeadersConstants;
import com.mercadolibre.controllers.PaymentsController;
import com.mercadolibre.controllers.PreferencesController;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.utils.datadog.DatadogRequestMetric;
import com.mercadolibre.utils.logs.LogBuilder;
import com.mercadolibre.utils.logs.MonitoringUtils;
import com.mercadolibre.utils.newRelic.NewRelicUtils;
import com.newrelic.api.agent.NewRelic;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.servlet.SparkApplication;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static com.mercadolibre.constants.HeadersConstants.API_CONTEXT;
import static com.mercadolibre.constants.HeadersConstants.REQUEST_ID;
import static spark.Spark.afterAfter;

public class Router implements SparkApplication {

    private static final Logger LOG = Logger.getLogger(Router.class);
    private static final String REQUEST_START_HEADER = "request-start";
    private static final String CONTETBNT_ENCODING_GZIP = "gzip";

    private static final String INTERNAL_ERROR = "internal error";
    private static final int CLIENT_CLOSE_REQUEST = 499;

    @Override
    public void init() {

        Spark.before("/*", (request, response) -> {
            final String scope = Config.getSCOPE();
            request.attribute(API_CONTEXT, ApiContext.getApiContextFromScope(scope));

            String requestId = request.headers(REQUEST_ID);
            if (StringUtils.isBlank(requestId)) {
                requestId = UUID.randomUUID().toString();
                LOG.info("Start new requestId : " + requestId);
            }
            request.attribute(REQUEST_ID, requestId);
            MDC.put(REQUEST_ID, requestId);
            MonitoringUtils.logRequest(request);
        });

        setupFilters();

        Spark.get("/ping", (req, res) -> {
            NewRelic.ignoreTransaction();
            res.status(HttpServletResponse.SC_OK);
            res.type(MediaType.PLAIN_TEXT_UTF_8.toString());
            return "pong";
        });

        Spark.path("/px_mobile", () -> {

            Spark.post("/payments", new MeteredRoute(PaymentsController.INSTANCE::doPayment,
                    "/payments"), GsonWrapper::toJson);

            Spark.get("/init/preference", new MeteredRoute(PreferencesController.INSTANCE::initCheckoutByPref,
                    "/init/preference"), GsonWrapper::toJson);

            Spark.exception(ApiException.class, (exception, request, response) -> {
                response.status(exception.getStatusCode());
                response.type(MediaType.JSON_UTF_8.toString());
                response.body(GsonWrapper.toJson(exception.toApiError()));
                if (exception.getStatusCode() < HttpStatus.SC_BAD_REQUEST) {
                    LOG.info(exception.toLog());
                } else {
                    LOG.error(exception.toLog());
                    if (exception.getStatusCode() >= CLIENT_CLOSE_REQUEST) {
                        NewRelicUtils.noticeError(exception, request);
                    }
                }
            });

            Spark.exception(Exception.class, (exception, request, response) -> {
                final String apiErrorJson = GsonWrapper.toJson(new ApiError(INTERNAL_ERROR,
                        "internal_error", HttpStatus.SC_INTERNAL_SERVER_ERROR));
                final LogBuilder logBuilder = new LogBuilder(LogBuilder.LEVEL_ERROR, LogBuilder.REQUEST_IN)
                        .withStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                        .withException(exception);
                LOG.error(logBuilder.build(), exception);
                NewRelicUtils.noticeError(exception, request);
                response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                response.type(MediaType.JSON_UTF_8.toString());
                response.body(apiErrorJson);

            });

            Spark.exception(ValidationException.class, (exception, request, response) -> {
                response.status(HttpStatus.SC_NOT_FOUND);
                response.type(MediaType.JSON_UTF_8.toString());
                ApiError apiError = new ApiError(exception.getMessage(), "bad request", response.status());
                response.body(GsonWrapper.toJson(apiError));
                LOG.error(exception.getMessage());
                NewRelicUtils.noticeError(exception, request);
            });

            Spark.after((req, res) -> {
                res.header(HttpHeaders.CONTENT_ENCODING, CONTETBNT_ENCODING_GZIP);
            });
        });

        afterAfter(DatadogRequestMetric::incrementRequestCounter);
    }

    private void setupFilters() {

        Spark.before((request, response) -> {
            request.attribute(REQUEST_START_HEADER, System.currentTimeMillis());
        });

        Spark.after(Router::setHeaders);

    }

    private static void setHeaders(final Request request, final Response response) {
        if (response.type() == null) {
            response.type(MediaType.JSON_UTF_8.toString());
        }

        response.header(HttpHeaders.VARY, "Accept,Accept-Encoding");

        // Has the endpoint decided on cacheability?
        if (response.raw().getHeader(HttpHeaders.CACHE_CONTROL) == null) {
            // No cache by default
            response.header(HttpHeaders.CACHE_CONTROL, HeadersConstants.NO_CACHE_PARAMS);
        }
    }

}
