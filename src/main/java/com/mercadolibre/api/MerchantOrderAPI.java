package com.mercadolibre.api;

import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.merchant_orders.MerchantOrder;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.dto.Context;
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

import static com.mercadolibre.utils.HeadersUtils.getHeaders;

public enum MerchantOrderAPI {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger();
    public static final String POOl_NAME = "MerchantOrdersWrite";
    public static final String URL = "/merchant_orders";

    static {
        RESTUtils.registerPool(POOl_NAME, pool ->
                pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("merchant_orders.socket.timeout"))
        );
    }

    @Trace
    public Either<MerchantOrder, ApiError> createMerchantOrder(final Context context, final MerchantOrder merchantOrderRequest,
                                                               final String collectorId) throws ApiException {
        final URIBuilder url = buildUrl(collectorId);
        final Headers headers = getHeaders(context.getRequestId());
        final String body = GsonWrapper.toJson(merchantOrderRequest);

        try {
            final Response response = RESTUtils.newRestRequestBuilder(POOl_NAME)
                    .post(url.toString(), headers, body.getBytes(StandardCharsets.UTF_8));
            return buildResponse(context.getRequestId(), headers, url, response);
        } catch (RestException e) {
            logger.error(LogUtils.getExceptionLog(context.getRequestId(), HttpMethod.POST.name(), POOl_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), e));
            throw new ApiException(ErrorsConstants.EXTERNAL_ERROR, "API call to payments failed", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    static URIBuilder buildUrl(final String callerId) {
        return new URIBuilder()
                .setScheme(Config.getString("merchant_orders.url.scheme"))
                .setHost(Config.getString("merchant_orders.url.host"))
                .setPath(URL)
                .addParameter("caller.id", String.valueOf(callerId));
    }

    private Either<MerchantOrder, ApiError> buildResponse(final String requestId, final Headers headers, final URIBuilder url, final Response response) {
        if (response.getStatus() < HttpStatus.SC_BAD_REQUEST) {
            logger.info(LogUtils.getResponseLog(requestId, HttpMethod.POST.name(), POOl_NAME, url.toString(), headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
        } else {
            logger.error(LogUtils.getResponseLogWithBody(requestId, HttpMethod.POST.name(), POOl_NAME, url.toString(), headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
        }
        return RESTUtils.responseToEither(response, MerchantOrder.class);
    }

}
