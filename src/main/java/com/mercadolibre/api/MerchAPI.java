package com.mercadolibre.api;

import com.mercadolibre.config.Config;
import com.mercadolibre.constants.HeadersConstants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.dto.congrats.merch.MerchResponse;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.px.toolkit.utils.DatadogUtils;
import com.mercadolibre.px.toolkit.utils.logs.LogUtils;
import com.mercadolibre.rest.RESTUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.utils.Either;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mercadolibre.constants.Constants.*;
import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_SITE_ID;

public enum MerchAPI {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger();
    private static final String URL = "/merch/middle-end/congrats/content";
    private static final String POOL_NAME = "MerchRead";
    private static final String PAYMENT_IDS = "paymentIds";
    private static final String LIMIT = "limit";
    private static final String DISCOUNTS_LIMIT = "6";

    static {
        RESTUtils.registerPool(POOL_NAME, pool ->
                pool.withConnectionTimeout(Config.getLong("merch.connection.timeout"))
                        .withSocketTimeout(Config.getLong("merch.socket.timeout"))
        );
    }

    /**
     * Makes an API call to Merch API using a user id, payments ids, site id, platform version and gets cross selling and discounts.
     *
     * @param context context
     * @param congratsRequest request congrats
     * @return Discount and cross selling object or api error
     */
    public CompletableFuture<Either<MerchResponse, ApiError>> getAsyncCrossSellingAndDiscount(final Context context, final CongratsRequest congratsRequest) {

        final Headers headers = new Headers().add(HeadersConstants.REQUEST_ID, context.getRequestId());
        final URIBuilder url = buildUrl(congratsRequest);

        try {
            final CompletableFuture<Response> completableFutureResponse = RESTUtils.newRestRequestBuilder(POOL_NAME)
                    .asyncGet(url.toString(), headers);

            return completableFutureResponse.thenApply(response -> {
                DatadogUtils.metricCollector.incrementCounter(
                        REQUEST_OUT_COUNTER,
                        DatadogUtils.getRequestOutCounterTags(HttpMethod.GET.name(), POOL_NAME, response.getStatus())
                );
                return buildResponse(context, headers, url, response);
            });
        } catch (RestException e) {
            logger.error(LogUtils.getExceptionLog(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), e));
            DatadogUtils.metricCollector.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
            return CompletableFuture.completedFuture(Either.alternative(ApiError.EXTERNAL_API));
        }
    }

    private Either<MerchResponse, ApiError> buildResponse(final Context context, final Headers headers, final URIBuilder url, final Response response) {
        if (org.eclipse.jetty.http.HttpStatus.isSuccess(response.getStatus())) {
            logger.info(LogUtils.getResponseLog(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
        } else {
            logger.error(LogUtils.getResponseLogWithBody(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
        }
        return RESTUtils.responseToEither(response, MerchResponse.class);
    }


    /**
     * Builds the api call url central discounts
     *
     * @param congratsRequest request congrats
     * @return a string with the url
     */
    public static URIBuilder buildUrl(final CongratsRequest congratsRequest) {
        final URIBuilder uriBuilder = new URIBuilder()
                .setScheme(Config.getString("merch.url.scheme"))
                .setHost(Config.getString("merch.url.host"))
                .setPath(URL)
                .addParameter(CALLER_ID_PARAM, congratsRequest.getUserId())
                .addParameter(CLIENT_ID_PARAM, congratsRequest.getClientId())
                .addParameter(CALLER_SITE_ID, congratsRequest.getSiteId())
                .addParameter(PAYMENT_IDS, congratsRequest.getPaymentIds())
                .addParameter(LIMIT, DISCOUNTS_LIMIT);

        if (null != congratsRequest.getUserAgent().getVersion().getVersionName()) {
            uriBuilder.addParameter("platform.version", congratsRequest.getUserAgent().getVersion().getVersionName());
        }

        return uriBuilder;
    }

    public static Optional<MerchResponse> getMerchResponseFromFuture(final Context context, final CompletableFuture<Either<MerchResponse, ApiError>> future) {
        try {
            if (future.get().isValuePresent()){
                return Optional.ofNullable(future.get().getValue());
            } else  {
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error(LogUtils.getExceptionLog(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, new Headers().add(HeadersConstants.REQUEST_ID, context.getRequestId()), null, e));
            DatadogUtils.metricCollector.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
            return Optional.empty();
        }
    }
}
