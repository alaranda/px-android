package com.mercadolibre.api;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.merchant_orders.MerchantOrder;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.config.Config;
import com.mercadolibre.px.toolkit.constants.ErrorCodes;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
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

import java.nio.charset.StandardCharsets;

import static com.mercadolibre.constants.Constants.API_CALL_PAYMENTS_FAILED;
import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;
import static com.mercadolibre.utils.HeadersUtils.getHeaders;
import static org.eclipse.jetty.http.HttpStatus.isSuccess;

public enum MerchantOrderAPI {
    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String POOL_NAME = "MerchantOrdersWrite";
    private static final String URL = "/merchant_orders";

    static {
        RestUtils.registerPool(POOL_NAME, pool ->
                pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("merchant_orders.socket.timeout"))
        );
    }

    /**
     * Apicall to merchant order
     *
     * @param context context object
     * @param merchantOrderRequest merchant order body
     * @param collectorId collector id
     * @return EitherPaymentApiError
     * @throws ApiException (optional) if the api call fail
     */
    @Trace(dispatcher = true, nameTransaction = true)
    public Either<MerchantOrder, ApiError> createMerchantOrder(final Context context, final MerchantOrder merchantOrderRequest,
                                                               final String collectorId) throws ApiException {
        final URIBuilder url = buildUrl(collectorId);
        final Headers headers = getHeaders(context.getRequestId());
        final String body = GsonWrapper.toJson(merchantOrderRequest);

        try {
            final Response response = RestUtils.newRestRequestBuilder(POOL_NAME)
                    .post(url.toString(), headers, body.getBytes(StandardCharsets.UTF_8));

            METRIC_COLLECTOR.incrementCounter(
                    REQUEST_OUT_COUNTER,
                    DatadogUtils.getRequestOutCounterTags(HttpMethod.POST.name(), POOL_NAME, response.getStatus())
            );

            return buildResponse(context.getRequestId(), headers, url, response);
        } catch (RestException e) {
            LOGGER.error(
                    LogUtils.getExceptionLog(
                            context.getRequestId(),
                            HttpMethod.POST.name(),
                            POOL_NAME,
                            URL,
                            headers,
                            LogUtils.convertQueryParam(url.getQueryParams()),
                            HttpStatus.SC_GATEWAY_TIMEOUT,
                            e));
            METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
            throw new ApiException(ErrorCodes.EXTERNAL_ERROR, API_CALL_PAYMENTS_FAILED, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Builds the api call url using the preference id
     *
     * @param callerId caller id
     * @return a string with the url
     */
    static URIBuilder buildUrl(final String callerId) {
        return new URIBuilder()
                .setScheme(Config.getString("merchant_orders.url.scheme"))
                .setHost(Config.getString("merchant_orders.url.host"))
                .setPath(URL)
                .addParameter("caller.id", String.valueOf(callerId));
    }

    private Either<MerchantOrder, ApiError> buildResponse(final String requestId, final Headers headers, final URIBuilder url, final Response response) {

        if (isSuccess(response.getStatus())) {
            LOGGER.info(
                    LogUtils.getResponseLogWithoutResponseBody(
                            requestId,
                            HttpMethod.POST.name(),
                            POOL_NAME,
                            url.toString(),
                            headers,
                            LogUtils.convertQueryParam(url.getQueryParams()),
                            response));
        } else {
            LOGGER.error(
                    LogUtils.getResponseLogWithResponseBody(
                            requestId,
                            HttpMethod.POST.name(),
                            POOL_NAME,
                            url.toString(),
                            headers,
                            LogUtils.convertQueryParam(url.getQueryParams()),
                            response));
        }
        return RestUtils.responseToEither(response, MerchantOrder.class);
    }

}