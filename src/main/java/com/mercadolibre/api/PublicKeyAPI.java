package com.mercadolibre.api;

import com.google.common.annotations.VisibleForTesting;
import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.public_key.PublicKeyInfo;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.rest.RESTUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.logs.MonitoringUtils;
import com.newrelic.api.agent.Trace;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;

import java.util.concurrent.CompletableFuture;

import static com.mercadolibre.utils.HeadersUtils.getHeaders;

public enum PublicKeyAPI {

    INSTANCE;

    private static final String PATH = "/v1/public_key";
    private static final String POOL = "PUBLIC_KEY_SERVICE_REST_POOL";

    static {
        RESTUtils.registerPool(POOL, pool ->
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
     * @param requestId request id
     * @param publicKey public key id
     * @return a CompletableFuture<Either<PublicKeyInfo, ApiError>>
     * @throws ApiException (optional) if the api call fail
     */
    @Trace
    public CompletableFuture<Either<PublicKeyInfo, ApiError>> getAsyncById(final String requestId, final String publicKey) throws ApiException {
        final Headers headers = getHeaders(requestId);
        final URIBuilder url = getPath(publicKey);

        try {
            final CompletableFuture<Response> completableFutureResponse = RESTUtils.newRestRequestBuilder(POOL).asyncGet(url.toString(), headers);
            return completableFutureResponse.thenApply(response -> buildResponse(headers, url, response));
        } catch (final RestException e) {
            MonitoringUtils.logException(HttpMethod.GET.name(), POOL, url.toString(), headers, e);
            throw new ApiException("external_error", "API call to public key failed", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private Either<PublicKeyInfo, ApiError> buildResponse(final Headers headers, final URIBuilder url, final Response response) {
        MonitoringUtils.logWithoutResponseBody(HttpMethod.GET.name(), POOL, PATH, url.getQueryParams(), response, headers);
        return RESTUtils.responseToEither(response, PublicKeyInfo.class);
    }


    /**
     * Makes an API call to Public Key API using a caller id and client id and gets all the data associated to the key.
     * The model PublicKey will be returned.
     * If an error occurs while parsing the response then null is returned.
     *
     * @param requestId request id
     * @param callerId caller id
     * @param clientId client id
     * @return a CompletableFuture<Either<PublicKeyInfo, ApiError>>
     * @throws ApiException (optional) if the api call fail
     */
    @Trace
    public Either<PublicKeyInfo, ApiError> getBycallerIdAndClientId(final String requestId, final String callerId,
                                                                    final Long clientId)
            throws ApiException {

        final Headers headers = getHeaders(requestId);
        final URIBuilder url = getPathWithParams(callerId, clientId);

        try {
            final Response serviceResponse = RESTUtils.newRestRequestBuilder(POOL).get(url.toString());
            return buildResponse(headers, url, serviceResponse);
        } catch (final RestException e) {
            MonitoringUtils.logException(HttpMethod.GET.name(), POOL, url.toString(), headers, e);
            throw new ApiException("external_error", "API call to public key failed", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @VisibleForTesting
    static URIBuilder getPath(String publicKey) {
        return new URIBuilder()
                .setScheme(Config.getString(Constants.PUBLIC_KEY_URL_SCHEME))
                .setHost(Config.getString(Constants.PUBLIC_KEY_URL_HOST))
                .setPath((PATH + "/" + publicKey));
    }


    @VisibleForTesting
    static URIBuilder getPathWithParams(final String callerId, final Long clientId) {
        return new URIBuilder()
                .setScheme(Config.getString(Constants.PUBLIC_KEY_URL_SCHEME))
                .setHost(Config.getString(Constants.PUBLIC_KEY_URL_HOST))
                .setPath(PATH)
                .addParameter("caller.id", callerId)
                .addParameter("client.id", String.valueOf(clientId));
    }

}