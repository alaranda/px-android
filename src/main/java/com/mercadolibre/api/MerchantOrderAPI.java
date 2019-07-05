package com.mercadolibre.api;

import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.merchant_orders.MerchantOrder;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.rest.RESTUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.logs.MonitoringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;

import java.nio.charset.StandardCharsets;

import static com.mercadolibre.utils.HeadersUtils.getHeaders;

public enum MerchantOrderAPI {

    INSTANCE;

    public static final String POOL_WRITE_NAME = "MerchantOrdersWrite";
    public static final String URL = "/merchant_orders";

    static {
        RESTUtils.registerPool(POOL_WRITE_NAME, pool ->
                pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("merchant_orders.socket.timeout"))
        );
    }

    public Either<MerchantOrder, ApiError> createMerchantOrder(final String requestId, final MerchantOrder merchantOrderRequest,
                                                               final String collectorId) throws ApiException {
        final URIBuilder url = buildUrl(collectorId);
        final Headers headers = getHeaders(requestId);
        final String body = GsonWrapper.toJson(merchantOrderRequest);

        try {
            final Response response = RESTUtils.newRestRequestBuilder(POOL_WRITE_NAME)
                    .post(url.toString(), headers, body.getBytes(StandardCharsets.UTF_8));
            return buildResponse(headers, url, response);
        } catch (RestException e) {
            MonitoringUtils.logException(HttpMethod.POST.name(), POOL_WRITE_NAME, url.toString(), headers, e);
            throw new ApiException("external_error", "API call to payments failed", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    static URIBuilder buildUrl(final String callerId) {
        return new URIBuilder()
                .setScheme(Config.getString("merchant_orders.url.scheme"))
                .setHost(Config.getString("merchant_orders.url.host"))
                .setPath(URL)
                .addParameter("caller.id", String.valueOf(callerId));
    }

    private Either<MerchantOrder, ApiError> buildResponse(final Headers headers, final URIBuilder url, final Response response) {
        MonitoringUtils.logWithoutResponseBody(HttpMethod.POST.name(), POOL_WRITE_NAME, url.toString(), url.getQueryParams(), response, headers);
        return RESTUtils.responseToEither(response, MerchantOrder.class);
    }

}
