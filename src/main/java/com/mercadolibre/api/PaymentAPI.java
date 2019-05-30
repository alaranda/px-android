package com.mercadolibre.api;

import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentBody;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.rest.RESTUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.logs.MonitoringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;

import java.nio.charset.StandardCharsets;

public enum PaymentAPI {

    INSTANCE;

    public static final String URL = "/v1/payments";
    public static final String POOL_WRITE_NAME = "PaymentsWrite";

    static {
        RESTUtils.registerPool(POOL_WRITE_NAME, pool ->
                pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("payment.socket.timeout"))
                        .withRetryStrategy(
                                new SimpleRetryStrategy(Config.getInt("payment.retries"),
                                        Config.getLong("payment.retry.delay")))
        );
    }

    public Either<Payment, ApiError> doPayment(final Long callerId, final Long clientId, final PaymentBody body,
                                               final Headers headers) throws ApiException {
        final URIBuilder url = buildUrl(callerId, clientId);

        try {
            final Response response = RESTUtils.newRestRequestBuilder(POOL_WRITE_NAME)
                    .post(url.toString(), headers, GsonWrapper.toJson(body).getBytes(StandardCharsets.UTF_8));
            return buildResponse(headers, url, response);
        } catch (RestException e) {
            MonitoringUtils.logException(HttpMethod.GET.name(), POOL_WRITE_NAME, url.toString(), headers, e);
            throw new ApiException("external_error", "API call to payments failed", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    static URIBuilder buildUrl(final Long callerId, final Long clientId) {
        return new URIBuilder()
                .setScheme(Config.getString("payment.url.scheme"))
                .setHost(Config.getString("payment.url.host"))
                .setPath(URL)
                .addParameter("caller.id", String.valueOf(callerId))
                .addParameter("client.id", String.valueOf(clientId));
    }

    private Either<Payment, ApiError> buildResponse(final Headers headers, final URIBuilder url, final Response response) {
        MonitoringUtils.logWithoutResponseBody(HttpMethod.GET.name(), POOL_WRITE_NAME, url.toString(), url.getQueryParams(), response, headers);
        return RESTUtils.responseToEither(response, Payment.class);
    }
}
