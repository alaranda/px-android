package com.mercadolibre.api;

import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.merchant_orders.MerchantOrder;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.px.toolkit.utils.DatadogUtils;
import com.mercadolibre.px.toolkit.utils.logs.LogUtils;
import com.mercadolibre.rest.RESTUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.ErrorsConstants;
import com.newrelic.api.agent.Trace;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.utils.HeadersUtils.getHeaders;

public enum MerchantOrderAPI {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger();
    private static final String POOL_NAME = "MerchantOrdersWrite";
    private static final String URL = "/merchant_orders";

    static {
        RESTUtils.registerPool(POOL_NAME, pool ->
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
            final Response response = RESTUtils.newRestRequestBuilder(POOL_NAME)
                    .post(url.toString(), headers, body.getBytes(StandardCharsets.UTF_8));

            DatadogUtils.metricCollector.incrementCounter(
                    REQUEST_OUT_COUNTER,
                    DatadogUtils.getRequestOutCounterTags(HttpMethod.POST.name(), POOL_NAME, response.getStatus())
            );

            return buildResponse(context.getRequestId(), headers, url, response);
        } catch (RestException e) {
            logger.error(LogUtils.getExceptionLog(context.getRequestId(), HttpMethod.POST.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), e));
            DatadogUtils.metricCollector.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
            throw new ApiException(ErrorsConstants.EXTERNAL_ERROR, "API call to payments failed", HttpStatus.SC_INTERNAL_SERVER_ERROR);
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

        if (response.getStatus() < HttpStatus.SC_BAD_REQUEST) {
            logger.info(LogUtils.getResponseLog(requestId, HttpMethod.POST.name(), POOL_NAME, url.toString(), headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
        } else {
            logger.error(LogUtils.getResponseLogWithBody(requestId, HttpMethod.POST.name(), POOL_NAME, url.toString(), headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
        }
        return RESTUtils.responseToEither(response, MerchantOrder.class);
    }

}