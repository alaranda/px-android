package com.mercadolibre.api;

import com.google.common.annotations.VisibleForTesting;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.user.PublicKey;
import com.mercadolibre.px.toolkit.config.Config;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.px.toolkit.utils.RestUtils;
import com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import com.newrelic.api.agent.Trace;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

import static com.mercadolibre.constants.Constants.API_CALL_PUBLIC_KEY_FAILED;
import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;
import static com.mercadolibre.utils.HeadersUtils.getHeaders;
import static org.eclipse.jetty.http.HttpStatus.isSuccess;

public enum PublicKeyAPI {
    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String URL = "/v1/public_key";
    private static final String POOL_NAME = "PublicKeyRead";

    static {
        RestUtils.registerPool(POOL_NAME, pool ->
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
    @Trace(async = true, dispatcher = true, nameTransaction = true)
    public CompletableFuture<Either<PublicKey, ApiError>> getAsyncById(final Context context, final String publicKey) throws ApiException {
        final Headers headers = getHeaders(context.getRequestId());
        final URIBuilder url = getPath(publicKey);

        try {
            final CompletableFuture<Response> completableFutureResponse = RestUtils.newRestRequestBuilder(POOL_NAME).asyncGet(url.toString(), headers);
            return completableFutureResponse.thenApply(response -> {
                    METRIC_COLLECTOR.incrementCounter(
                            REQUEST_OUT_COUNTER,
                            DatadogUtils.getRequestOutCounterTags(HttpMethod.GET.name(), POOL_NAME, response.getStatus())
                    );
                    return buildResponse(context, headers, url, response);
            });
        } catch (final RestException e) {
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
            throw new ApiException(EXTERNAL_ERROR, API_CALL_PUBLIC_KEY_FAILED, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private Either<PublicKey, ApiError> buildResponse(final Context context, final Headers headers, final URIBuilder url, final Response response) {
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
        return RestUtils.responseToEither(response, PublicKey.class);
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
    @Trace(dispatcher = true, nameTransaction = true)
    public Either<PublicKey, ApiError> getBycallerIdAndClientId(final Context context, final String callerId,
                                                                final Long clientId)
            throws ApiException {

        final Headers headers = getHeaders(context.getRequestId());
        final URIBuilder url = getPathWithParams(callerId, clientId);

        try {
            final Response response = RestUtils.newRestRequestBuilder(POOL_NAME).get(url.toString());
            METRIC_COLLECTOR.incrementCounter(
                    REQUEST_OUT_COUNTER,
                    DatadogUtils.getRequestOutCounterTags(HttpMethod.GET.name(), POOL_NAME, response.getStatus())
            );
            return buildResponse(context, headers, url, response);
        } catch (final RestException e) {
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
            throw new ApiException(EXTERNAL_ERROR, API_CALL_PUBLIC_KEY_FAILED, HttpStatus.SC_INTERNAL_SERVER_ERROR);
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