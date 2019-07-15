package com.mercadolibre.api;

import com.google.common.net.HttpHeaders;
import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.preference.PreferenceTidy;
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
import com.newrelic.api.agent.Trace;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;

import static com.mercadolibre.constants.HeadersConstants.X_REQUEST_ID;

public enum PreferenceTidyApi {

    INSTANCE;

    public static final String POOL_READ_NAME = "preferenceTidyRead";
    public static final String URL = "/tidy/";

    static {
        RESTUtils.registerPool(POOL_READ_NAME, pool ->
                pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("preference_tidy.socket.timeout"))
                        .withRetryStrategy(
                                new SimpleRetryStrategy(Config.getInt("preference_tidy.retries"),
                                        Config.getLong("preference_tidy.retry.delay")))
        );
    }


    /**
     * Hace el API call a Preference Tidy para obtener el id de la preferencia
     *
     * @param requestId id del request
     * @return preferenceTidy
     * @throws ApiException  si falla el api call (status code is not 2xx)
     */
    @Trace
    public PreferenceTidy getPreferenceByKey(final String requestId,
                                    final String key) throws ApiException {
        final Headers headers = new Headers().add(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .add(X_REQUEST_ID, requestId);
        final String url = buildUrl(key);
        try {
            final Response response = RESTUtils.newRestRequestBuilder(POOL_READ_NAME).get(url, headers);
            MonitoringUtils.logWithoutResponseBody(HttpMethod.GET.name(), POOL_READ_NAME, url, response, headers);
            if (RESTUtils.isResponseSuccessful(response)) {
                return RESTUtils.responseToObject(response, PreferenceTidy.class);
            }
            throw new ApiException(GsonWrapper.fromJson(RESTUtils.getBody(response), ApiError.class));
        } catch (final RestException e) {
            MonitoringUtils.logException(HttpMethod.GET.name(), POOL_READ_NAME, URL, headers, e);
            throw new ApiException("external_error", "API call to preferenceTidy failed", HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
        }
    }


    /**
     * Builds the api call url using the preference id
     *
     * @param preferenceKey preference id
     * @return a string with the url
     */
    public static String buildUrl(final String preferenceKey) {
        return new URIBuilder()
                .setScheme(Config.getString("preference_tidy.url.scheme"))
                .setHost(Config.getString("preference_tidy.url.host"))
                .setPath(String.format(URL.concat(preferenceKey))).toString();
    }

}
