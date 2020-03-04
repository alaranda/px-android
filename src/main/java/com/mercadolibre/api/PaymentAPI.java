package com.mercadolibre.api;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentBody;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.config.Config;
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
import java.util.concurrent.ExecutionException;

import static com.mercadolibre.constants.Constants.API_CALL_PAYMENTS_FAILED;
import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;
import static org.eclipse.jetty.http.HttpStatus.isSuccess;


public enum PaymentAPI {
    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String URL = "/v1/payments";
    private static final String POOL_NAME = "PaymentsWrite";
    private static final String POOL_NAME_READ = "PaymentsRead";

    static {
        RestUtils.registerPool(POOL_NAME, pool ->
                    pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("payment.socket.timeout"))
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
    @Trace(dispatcher = true, nameTransaction = true)
    public Either<Payment, ApiError> doPayment(final Context context, final Long callerId, final Long clientId, final PaymentBody body,
                                               final Headers headers) throws ApiException {
        final URIBuilder url = buildUrl(callerId, clientId);

        try {
            final Response response = RestUtils.newRestRequestBuilder(POOL_NAME)
                    .asyncPost(url.toString(), headers, GsonWrapper.toJson(body).getBytes(StandardCharsets.UTF_8)).get();

            METRIC_COLLECTOR.incrementCounter(
                    REQUEST_OUT_COUNTER,
                    DatadogUtils.getRequestOutCounterTags(HttpMethod.POST.name(), POOL_NAME, response.getStatus())
            );

            return buildResponse(context, headers, url, response, HttpMethod.POST.name(), POOL_NAME);
        } catch (final RestException | InterruptedException | ExecutionException e) {
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
            throw new ApiException(EXTERNAL_ERROR, API_CALL_PAYMENTS_FAILED, HttpStatus.SC_BAD_GATEWAY);
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


    /**
     * Apicall to payments
     *
     * @param context context object
     * @param paymentId payment id
     * @return EitherPaymentApiError
     * @throws ApiException (optional) if the api call fail
     */
    @Trace(dispatcher = true, nameTransaction = true)
    public Either<Payment, ApiError> getPayment(final Context context, final String paymentId) throws ApiException {

        final Headers headers = new Headers().add(REQUEST_ID, context.getRequestId());
        final URIBuilder url = buildGetPaymentUrl(paymentId);

        try {
            final Response response = RestUtils.newRestRequestBuilder(POOL_NAME)
                    .asyncGet(url.toString(), headers).get();

            METRIC_COLLECTOR.incrementCounter(
                    REQUEST_OUT_COUNTER,
                    DatadogUtils.getRequestOutCounterTags(HttpMethod.GET.name(), POOL_NAME_READ, response.getStatus())
            );

            return buildResponse(context, headers, url, response, HttpMethod.GET.name(), POOL_NAME_READ);
        } catch (final RestException | InterruptedException | ExecutionException e) {
            LOGGER.error(
                    LogUtils.getExceptionLog(
                            context.getRequestId(),
                            HttpMethod.GET.name(),
                            POOL_NAME_READ,
                            url.toString(),
                            headers,
                            LogUtils.convertQueryParam(url.getQueryParams()),
                            HttpStatus.SC_GATEWAY_TIMEOUT,
                            e));
            METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME_READ);
            throw new ApiException(EXTERNAL_ERROR, API_CALL_PAYMENTS_FAILED,  HttpStatus.SC_BAD_GATEWAY);
        }
    }

    /**
     * Builds the api call url using the preference id
     *
     * @param paymentId payment id
     * @return a string with the url
     */
    static URIBuilder buildGetPaymentUrl(final String paymentId) {
        return new URIBuilder()
                .setScheme(Config.getString("payment.url.scheme"))
                .setHost(Config.getString("payment.url.host"))
                .setPath(URL.concat("/" + paymentId))
                .addParameter("caller.scopes", "payments,admin");
    }


    private Either<Payment, ApiError> buildResponse(final Context context, final Headers headers, final URIBuilder url, final Response response) {
        if (isSuccess(response.getStatus())) {
            LOGGER.info(
                    LogUtils.getResponseLogWithoutResponseBody(
                            context.getRequestId(),
                            HttpMethod.POST.name(),
                            POOL_NAME,
                            url.toString(),
                            headers,
                            LogUtils.convertQueryParam(url.getQueryParams()),
                            response));
        } else {
            LOGGER.error(
                    LogUtils.getResponseLogWithResponseBody(
                            context.getRequestId(),
                            HttpMethod.POST.name(),
                            POOL_NAME,
                            url.toString(),
                            headers,
                            LogUtils.convertQueryParam(url.getQueryParams()),
                            response));
        }

}
