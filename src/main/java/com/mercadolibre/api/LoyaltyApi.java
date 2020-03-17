package com.mercadolibre.api;

import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.dto.congrats.Points;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.config.Config;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.px.toolkit.utils.RestUtils;
import com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.newrelic.api.agent.Trace;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;
import static org.eclipse.jetty.http.HttpStatus.isSuccess;

public enum LoyaltyApi {
    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String URL = "/loyal_middleend/congrats";
    private static final String POOL_NAME = "LoyalRead";

    static {
        RestUtils.registerPool(POOL_NAME, pool ->
                pool.withConnectionTimeout(Config.getLong("loyal.connection.timeout"))
                        .withSocketTimeout(Config.getLong("loyal.socket.timeout"))
        );
    }

    /**
     * Makes an API call to Loyal API using a user id, payment id, site id, platform and gets all the points associated to the user and payment.
     * If an error occurs while parsing the response then null is returned.
     *
     * @param context context
     * @param congratsRequest request congrats
     * @return CompletableFutureEitherPointsApiError
     */
    @Trace(async = true, dispatcher = true, nameTransaction = true)
    public CompletableFuture<Either<Points, ApiError>> getAsyncPoints(final Context context, final CongratsRequest congratsRequest) {

        final Headers headers = addHeaders(context, congratsRequest.getUserAgent());
        final URIBuilder url = buildUrl(congratsRequest);
        try {
            final CompletableFuture<Response> completableFutureResponse = RestUtils.newRestRequestBuilder(POOL_NAME).asyncGet(url.toString(), headers);

            return completableFutureResponse.thenApply(response -> {
                METRIC_COLLECTOR.incrementCounter(
                        REQUEST_OUT_COUNTER,
                        DatadogUtils.getRequestOutCounterTags(HttpMethod.GET.name(), POOL_NAME, response.getStatus())
                );
                return buildResponse(context, headers, url, response);
            });
        } catch (RestException e) {
            LOGGER.error(
                    LogUtils.getExceptionLog(
                            context.getRequestId(),
                            HttpMethod.GET.name(),
                            POOL_NAME,
                            URL,
                            headers,
                            LogUtils.convertQueryParam(url.getQueryParams()),
                            HttpStatus.SC_GATEWAY_TIMEOUT,
                            e));
            METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
            return CompletableFuture.completedFuture(Either.alternative(ApiError.EXTERNAL_API));
        }
    }

    private Either<Points, ApiError> buildResponse(final Context context, final Headers headers, final URIBuilder url, final Response response) {
        if (isSuccess(response.getStatus())) {
            LOGGER.info(
                    LogUtils.getResponseLogWithoutResponseBody(
                            context.getRequestId(),
                            HttpMethod.GET.name(),
                            POOL_NAME,
                            URL,
                            headers,
                            LogUtils.convertQueryParam(url.getQueryParams()),
                            response));
        } else {
            LOGGER.error(
                    LogUtils.getResponseLogWithResponseBody(
                            context.getRequestId(),
                            HttpMethod.GET.name(),
                            POOL_NAME,
                            URL,
                            headers,
                            LogUtils.convertQueryParam(url.getQueryParams()),
                            response));
        }
        return RestUtils.responseToEither(response, Points.class);
    }

    private static Headers addHeaders(final Context context, final UserAgent userAgent) {
        return new Headers()
                .add(REQUEST_ID, context.getRequestId())
                .add( "X-Client-Name", userAgent.getOperatingSystem().getName().toLowerCase())
                .add("X-Client-Version", "0.2");
    }


    /**
     * Builds the api call url using the loyalty
     *
     * @param congratsRequest request congrats
     * @return a string with the url
     */
    public static URIBuilder buildUrl(final CongratsRequest congratsRequest) {
        return new URIBuilder()
                .setScheme(Config.getString("loyal.url.scheme"))
                .setHost(Config.getString("loyal.url.host"))
                .setPath(URL)
                .addParameter("user_id", congratsRequest.getUserId())
                .addParameter("site_id", congratsRequest.getSiteId())
                .addParameter("payments_ids", congratsRequest.getPaymentIds())
                .addParameter("action", "payment")
                .addParameter("platform", congratsRequest.getPlatform());
    }

    public static Optional<Points> getPointsFromFuture(final Context context, final CompletableFuture<Either<Points, ApiError>> future) {
        try {
            if (null != future && future.get().isValuePresent()){
                return Optional.ofNullable(future.get().getValue());
            } else  {
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(
                    LogUtils.getExceptionLog(
                            context.getRequestId(),
                            HttpMethod.GET.name(),
                            POOL_NAME,
                            URL,
                            new Headers().add(REQUEST_ID, context.getRequestId()),
                            null,
                            HttpStatus.SC_GATEWAY_TIMEOUT,
                            e));
            METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
            return Optional.empty();
        }
    }
}
