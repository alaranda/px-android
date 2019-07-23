package com.mercadolibre.api;

import com.google.common.net.HttpHeaders;
import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.preference.PreferenceTidy;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.px.toolkit.utils.logs.LogUtils;
import com.mercadolibre.rest.RESTUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import com.mercadolibre.utils.ErrorsConstants;
import com.newrelic.api.agent.Trace;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.mercadolibre.constants.HeadersConstants.X_REQUEST_ID;

public enum PreferenceTidyApi {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger();
    public static final String POOL_NAME = "preferenceTidyRead";
    public static final String URL = "/tidy/";

    static {
        RESTUtils.registerPool(POOL_NAME, pool ->
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
     * @param context context
     * @return preferenceTidy
     * @throws ApiException  si falla el api call (status code is not 2xx)
     */
    @Trace
    public PreferenceTidy getPreferenceByKey(final Context context, final String key) throws ApiException {
        final Headers headers = new Headers().add(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .add(X_REQUEST_ID, context.getRequestId());
        final String url = buildUrl(key);
        try {
            final Response response = RESTUtils.newRestRequestBuilder(POOL_NAME).get(url, headers);

            if (response.getStatus() < HttpStatus.SC_BAD_REQUEST) {
                logger.info(LogUtils.getResponseLog(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, key, response));
                return RESTUtils.responseToObject(response, PreferenceTidy.class);
            } else {
                logger.error(LogUtils.getResponseLogWithBody(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, key, response));
            }
            throw new ApiException(GsonWrapper.fromJson(RESTUtils.getBody(response), ApiError.class));
        } catch (final RestException e) {
            logger.error(LogUtils.getExceptionLog(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, key, e));
            throw new ApiException(ErrorsConstants.EXTERNAL_ERROR, "API call to preferenceTidy failed", HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
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
