package com.mercadolibre.api;

import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentBody;
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
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.ErrorsConstants;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;

public enum PaymentAPI {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger();
    private static final String URL = "/v1/payments";
    private static final String POOL_NAME = "PaymentsWrite";

    private static final long MAX_IDLE_5_HOURS_IN_MS = 5 * 60 * 60 * 1000;

    static {
        RESTUtils.registerPool(POOL_NAME, pool ->
                    pool.withMaxIdleTime(MAX_IDLE_5_HOURS_IN_MS)
                        .withMaxTotal(30)
                        .withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("payment.socket.timeout"))
                        .withRetryStrategy(
                                new SimpleRetryStrategy(Config.getInt("payment.retries"),
                                        Config.getLong("payment.retry.delay")))
        );
    }

    /**
     * Apicall to payments
     *
     * @param context context object
     * @param callerId caller id
     * @param clientId client id
     * @param body body
     * @param headers headers
     * @return EitherPaymentApiError
     * @throws ApiException (optional) if the api call fail
     */
    public Either<Payment, ApiError> doPayment(final Context context, final Long callerId, final Long clientId, final PaymentBody body,
                                               final Headers headers) throws ApiException {
        final URIBuilder url = buildUrl(callerId, clientId);

        try {
            final Response response = RESTUtils.newRestRequestBuilder(POOL_NAME)
                    .post(url.toString(), headers, GsonWrapper.toJson(body).getBytes(StandardCharsets.UTF_8));

            DatadogUtils.metricCollector.incrementCounter(
                    REQUEST_OUT_COUNTER,
                    DatadogUtils.getRequestOutCounterTags(HttpMethod.POST.name(), POOL_NAME, response.getStatus())
            );

            return buildResponse(context, headers, url, response);
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
     * @param callerId client id
     * @return a string with the url
     */
    static URIBuilder buildUrl(final Long callerId, final Long clientId) {
        return new URIBuilder()
                .setScheme(Config.getString("payment.url.scheme"))
                .setHost(Config.getString("payment.url.host"))
                .setPath(URL)
                .addParameter("caller.id", String.valueOf(callerId))
                .addParameter("client.id", String.valueOf(clientId));
    }

    private Either<Payment, ApiError> buildResponse(final Context context, final Headers headers, final URIBuilder url, final Response response) {
        if (response.getStatus() < HttpStatus.SC_BAD_REQUEST) {
            logger.info(LogUtils.getResponseLog(context.getRequestId(), HttpMethod.POST.name(), POOL_NAME, url.toString(),headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
        } else {
            logger.error(LogUtils.getResponseLogWithBody(context.getRequestId(), HttpMethod.POST.name(), POOL_NAME, url.toString(),headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
        }
        return RESTUtils.responseToEither(response, Payment.class);
    }
}
