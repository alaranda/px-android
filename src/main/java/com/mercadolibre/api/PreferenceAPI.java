package com.mercadolibre.api;

import com.google.common.net.HttpHeaders;
import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.rest.RESTUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.ErrorsConstants;
import com.mercadolibre.utils.logs.MonitoringUtils;
import com.newrelic.api.agent.Trace;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;


import java.util.concurrent.CompletableFuture;

import static com.mercadolibre.constants.HeadersConstants.X_CALLER_SCOPES;
import static com.mercadolibre.constants.HeadersConstants.X_REQUEST_ID;

public enum PreferenceAPI {

    INSTANCE;

    public static final String POOL_READ_NAME = "PreferencesRead";
    public static final String URL = "/checkout/preferences";

    static {
        RESTUtils.registerPool(POOL_READ_NAME, pool ->
                pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("preference.socket.timeout"))
                        .withRetryStrategy(
                                new SimpleRetryStrategy(Config.getInt("preference.retries"),
                                        Config.getLong("preference.retry.delay")))
        );
    }


    /**
     * Hace el API call a Preference para obtener la preferencia
     *
     * @param requestId id del request
     * @return preference
     * @throws ApiException  si falla el api call (status code is not 2xx)
     */
    @Trace
    public CompletableFuture<Either<Preference, ApiError>> geAsynctPreference( final String preferenceId,
                                                                               final String requestId) throws ApiException {
        final Headers headers = new Headers().add(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .add(X_CALLER_SCOPES, "admin")
                .add(X_REQUEST_ID, requestId);
        final URIBuilder url = buildUrl(preferenceId);
        try {
            final CompletableFuture<Response> completableFutureResponse = RESTUtils.newRestRequestBuilder(POOL_READ_NAME).asyncGet(url.toString(), headers);
            return completableFutureResponse.thenApply(response -> buildResponse(headers, url, response));
        } catch (final RestException e) {
            MonitoringUtils.logException(HttpMethod.GET.name(), POOL_READ_NAME, url.toString(), headers, e);
            throw new ApiException(ErrorsConstants.EXTERNAL_ERROR, "API call to preference failed", HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    private Either<Preference, ApiError> buildResponse(final Headers headers, final URIBuilder url, final Response response) {
        MonitoringUtils.logWithoutResponseBody(HttpMethod.GET.name(), POOL_READ_NAME, URL, url.getQueryParams(), response, headers);
        return RESTUtils.responseToEither(response, Preference.class);
    }

    /**
     * Builds the api call url using the preference id
     *
     * @param preferenceId preference id
     * @return a string with the url
     */
    public static URIBuilder buildUrl(final String preferenceId) {
        return new URIBuilder()
                .setScheme(Config.getString("preference.url.scheme"))
                .setHost(Config.getString("preference.url.host"))
                .setPath(String.format("%s/%s", URL, preferenceId));
    }
}
