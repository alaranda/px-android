package com.mercadolibre.api;

import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.access_token.AccessToken;
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

import static com.mercadolibre.constants.HeadersConstants.REQUEST_ID;

public enum AccessTokenAPI {

    INSTANCE;

    public static final String POOL_READ_NAME = "AccessTokenRead";
    public static final String AT_URL = "/auth/access_token";

    static {
        RESTUtils.registerPool(POOL_READ_NAME, pool ->
                pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("access_token.socket.timeout"))
                        .withRetryStrategy(
                                new SimpleRetryStrategy(Config.getInt("access_token.retries"),
                                        Config.getLong("access_token.retry.delay")))
        );
    }

    /**
     * Makes an API call to Access Token API using a access token id and gets all the data associated to the key.
     * The model AccessToken will be returned.
     * If an error occurs while parsing the response then null is returned.
     *
     * @param accessTokenId access token id
     * @param requestId     request id
     * @return AccessToken object or api error
     * @throws ApiException (optional) if the api call fails
     */
    @Trace
    public Either<AccessToken, ApiError> getById(final String accessTokenId, final String requestId) throws ApiException {
        final Headers headers = new Headers().add(REQUEST_ID, requestId);
        final URIBuilder url = buildUrl(accessTokenId);

        try {
            final Response response = RESTUtils.newRestRequestBuilder(POOL_READ_NAME)
                    .get(url.toString(), headers);
            MonitoringUtils.logWithoutResponseBody(HttpMethod.GET.name(), POOL_READ_NAME, AT_URL, response, headers);
            return RESTUtils.responseToEither(response, AccessToken.class);
        } catch (RestException e) {
            MonitoringUtils.logException(HttpMethod.GET.name(), POOL_READ_NAME, AT_URL, url.getQueryParams(), headers, e);
            throw new ApiException("external_error", "API call to access token failed", HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    public static URIBuilder buildUrl(final String accessTokenId) {
        return new URIBuilder()
                .setScheme(Config.getString("access_token.url.scheme"))
                .setHost(Config.getString("access_token.url.host"))
                .setPath(AT_URL + "/" + accessTokenId);
    }

}
