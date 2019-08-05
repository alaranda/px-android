package com.mercadolibre.api;

import com.google.common.net.HttpHeaders;
import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.px.toolkit.utils.DatadogUtils;
import com.mercadolibre.px.toolkit.utils.logs.LogUtils;
import com.mercadolibre.rest.RESTUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.ErrorsConstants;
import com.newrelic.api.agent.Trace;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.concurrent.CompletableFuture;

import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.constants.HeadersConstants.X_CALLER_SCOPES;
import static com.mercadolibre.constants.HeadersConstants.X_REQUEST_ID;

public enum PreferenceAPI {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger();
    private static final String POOL_NAME = "PreferencesRead";
    private static final String URL = "/checkout/preferences";

    static {
        RESTUtils.registerPool(POOL_NAME, pool ->
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
     * @param context context object
     * @return preference
     * @throws ApiException  si falla el api call (status code is not 2xx)
     */
    @Trace
    public CompletableFuture<Either<Preference, ApiError>> geAsynctPreference(final Context context, final String preferenceId) throws ApiException {
        final Headers headers = new Headers().add(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .add(X_CALLER_SCOPES, "admin")
                .add(X_REQUEST_ID, context.getRequestId());
        final URIBuilder url = buildUrl(preferenceId);
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
            throw new ApiException(ErrorsConstants.EXTERNAL_ERROR, "API call to preference failed", HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    private Either<Preference, ApiError> buildResponse(final Context context, final Headers headers, final URIBuilder url, final Response response) {
        if (response.getStatus() < HttpStatus.SC_BAD_REQUEST) {
            logger.info(LogUtils.getResponseLog(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
        } else {
            logger.error(LogUtils.getResponseLogWithBody(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
        }
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
