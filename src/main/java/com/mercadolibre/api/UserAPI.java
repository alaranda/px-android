package com.mercadolibre.api;

import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.constants.HeadersConstants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.User;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.px.toolkit.utils.DatadogUtils;
import com.mercadolibre.px.toolkit.utils.logs.LogUtils;
import com.mercadolibre.rest.RESTUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.constants.HeadersConstants.X_CALLER_SCOPES;

public enum UserAPI {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger();
    private static final String URL = "/users";
    private static final String POOL_NAME = "USERS_SERVICE_REST_POOL";
    private static final String ADMIN= "admin";

    static {
        RESTUtils.registerPool(POOL_NAME, pool ->
                pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("users.socket.timeout"))
        );
    }

    /**
     * Makes an API call to Users API using a user id and gets all the data associated to the user.
     * The model User will be returned.
     * If an error occurs while parsing the response then null is returned.
     *
     * @param context    context
     * @param userId    user id
     * @return User object
     * @throws ApiException (optional) if the api call fails
     */
    public User getById(final Context context, final long userId) throws ApiException {
        final Headers headers = new Headers().add(HeadersConstants.REQUEST_ID, context.getRequestId());
        final URIBuilder url = buildUrl(userId);
        try {
            final Response response = RESTUtils.newRestRequestBuilder(POOL_NAME)
                    .get(url.toString(), headers);

            DatadogUtils.metricCollector.incrementCounter(
                    REQUEST_OUT_COUNTER,
                    DatadogUtils.getRequestOutCounterTags(HttpMethod.GET.name(), POOL_NAME, response.getStatus())
            );

            if (response.getStatus() < HttpStatus.SC_BAD_REQUEST) {
                logger.info(LogUtils.getResponseLog(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
                return RESTUtils.responseToObject(response, User.class);
            } else {
                logger.error(LogUtils.getResponseLogWithBody(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), response));
            }
            throw new ApiException(GsonWrapper.fromJson(RESTUtils.getBody(response), ApiError.class));

        } catch (RestException e) {
            logger.error(LogUtils.getExceptionLog(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), e));
            DatadogUtils.metricCollector.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
            throw new ApiException("external_error", "API call to users failed", HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
        }
    }


    /**
     * Builds the api call url using the user id
     *
     * @param userId preference id
     * @return a string with the url
     */
    public static URIBuilder buildUrl(final long userId) {
        return new URIBuilder()
                .setScheme(Config.getString(Constants.USERS_URL_SCHEME))
                .setHost(Config.getString(Constants.USERS_URL_HOST))
                .setPath(String.format("%s/%s", URL, String.valueOf(userId)))
                .addParameter(X_CALLER_SCOPES, ADMIN);
    }
}
