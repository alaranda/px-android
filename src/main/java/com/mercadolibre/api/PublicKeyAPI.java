package com.mercadolibre.api;

import com.google.common.annotations.VisibleForTesting;
import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.public_key.PublicKeyInfo;
import com.mercadolibre.exceptions.ApiException;
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

import java.util.concurrent.CompletableFuture;

import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.utils.HeadersUtils.getHeaders;

public enum PublicKeyAPI {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger();
    private static final String URL = "/v1/public_key";
    private static final String POOL_NAME = "PublicKeyRead";

    static {
        RESTUtils.registerPool(POOL_NAME, pool ->
                pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("public_key.socket.timeout"))
                        .withRetryStrategy(
                                new SimpleRetryStrategy(Config.getInt("public_key.retries"),
                                        Config.getLong("public_key.retry.delay")))
        );
    }

    /**
     * Makes an async API call to Public Key API using a public key and gets all the data associated to the key.
     * The model PublicKey will be returned.
     * If an error occurs while parsing the response then null is returned.
     *
     * @param context context object
     * @param publicKey public key id
     * @return CompletableFuture Either PublicKeyInfo ApiError
     * @throws ApiException (optional) if the api call fail
     */
    public CompletableFuture<Either<PublicKeyInfo, ApiError>> getAsyncById(final Context context, final String publicKey) throws ApiException {
        final Headers headers = getHeaders(context.getRequestId());
        final URIBuilder url = getPath(publicKey);

        try {
            final CompletableFuture<Response> completableFutureResponse = RESTUtils.newRestRequestBuilder(POOL_NAME).asyncGet(url.toString(), headers);
            return completableFutureResponse.thenApply(response -> {
                    DatadogUtils.metricCollector.incrementCounter(
                            REQUEST_OUT_COUNTER,
                            DatadogUtils.getRequestOutCounterTags(HttpMethod.GET.name(), POOL_NAME, response.getStatus())
                    );
                    return buildResponse(context, headers, url, response);
            });
        } catch (final RestException e) {
            logger.error(LogUtils.getExceptionLog(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), e));
            DatadogUtils.metricCollector.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
            throw new ApiException(ErrorsConstants.EXTERNAL_ERROR, "API call to public key failed", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private Either<PublicKeyInfo, ApiError> buildResponse(final Context context, final Headers headers, final URIBuilder url, final Response response) {
        if (response.getStatus() < HttpStatus.SC_BAD_REQUEST) {
            logger.info(LogUtils.getResponseLog(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
        } else {
            logger.error(LogUtils.getResponseLogWithBody(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
        }
        return RESTUtils.responseToEither(response, PublicKeyInfo.class);
    }


    /**
     * Makes an API call to Public Key API using a caller id and client id and gets all the data associated to the key.
     * The model PublicKey will be returned.
     * If an error occurs while parsing the response then null is returned.
     *
     * @param context context object
     * @param callerId caller id
     * @param clientId client id
     * @return CompletableFutureEitherPublicKeyInfoApiError
     * @throws ApiException (optional) if the api call fail
     */
    public Either<PublicKeyInfo, ApiError> getBycallerIdAndClientId(final Context context, final String callerId,
                                                                    final Long clientId)
            throws ApiException {

        final Headers headers = getHeaders(context.getRequestId());
        final URIBuilder url = getPathWithParams(callerId, clientId);

        try {
            final Response response = RESTUtils.newRestRequestBuilder(POOL_NAME).get(url.toString());
            DatadogUtils.metricCollector.incrementCounter(
                    REQUEST_OUT_COUNTER,
                    DatadogUtils.getRequestOutCounterTags(HttpMethod.GET.name(), POOL_NAME, response.getStatus())
            );
            return buildResponse(context, headers, url, response);
        } catch (final RestException e) {
            logger.error(LogUtils.getExceptionLog(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), e));
            DatadogUtils.metricCollector.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
            throw new ApiException("external_error", "API call to public key failed", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @VisibleForTesting
    static URIBuilder getPath(String publicKey) {
        return new URIBuilder()
                .setScheme(Config.getString(Constants.PUBLIC_KEY_URL_SCHEME))
                .setHost(Config.getString(Constants.PUBLIC_KEY_URL_HOST))
                .setPath((URL + "/" + publicKey));
    }


    @VisibleForTesting
    static URIBuilder getPathWithParams(final String callerId, final Long clientId) {
        return new URIBuilder()
                .setScheme(Config.getString(Constants.PUBLIC_KEY_URL_SCHEME))
                .setHost(Config.getString(Constants.PUBLIC_KEY_URL_HOST))
                .setPath(URL)
                .addParameter("caller.id", callerId)
                .addParameter("client.id", String.valueOf(clientId));
    }

}