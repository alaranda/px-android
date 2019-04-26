package com.mercadolibre.api;

import com.google.common.net.HttpHeaders;
import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.rest.RESTUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import com.mercadolibre.utils.logs.MonitoringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;

import java.util.Optional;

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
    public Optional<Preference> getPreference(final String requestId,
                                              final String preferenceId) throws ApiException {
        final Headers headers = new Headers().add(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .add(X_CALLER_SCOPES, "admin")
                .add(X_REQUEST_ID, requestId);
        final String url = buildUrl(preferenceId);
        try {
            final Response response = RESTUtils.newRestRequestBuilder(POOL_READ_NAME).get(url, headers);
            MonitoringUtils.logWithoutResponseBody(HttpMethod.GET.name(), POOL_READ_NAME, url, response, headers);
            if (RESTUtils.isResponseSuccessful(response)) {
                return Optional.ofNullable(RESTUtils.responseToObject(response, Preference.class));
            }
            throw new ApiException(GsonWrapper.fromJson(RESTUtils.getBody(response), ApiError.class));
        } catch (final RestException e) {
            MonitoringUtils.logException(HttpMethod.GET.name(), POOL_READ_NAME, url, headers, e);
            throw new ApiException("external_error", "API call to preference failed", HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * Builds the api call url using the preference id
     *
     * @param preferenceId preference id
     * @return a string with the url
     */
    public static String buildUrl(final String preferenceId) {
        return new URIBuilder()
                .setScheme(Config.getString("preference.url.scheme"))
                .setHost(Config.getString("preference.url.host"))
                .setPath(String.format("%s/%s", URL, preferenceId)).toString();
    }
}
