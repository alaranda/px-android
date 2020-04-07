package com.mercadolibre.api;

import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;
import static org.eclipse.jetty.http.HttpStatus.isSuccess;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.User;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.config.Config;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.utils.RestUtils;
import com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.newrelic.api.agent.Trace;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum UserAPI {
  INSTANCE;

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String URL = "/users";
  private static final String POOL_NAME = "USERS_SERVICE_REST_POOL";
  private static final String ADMIN = "admin";

  static {
    RestUtils.registerPool(
        POOL_NAME,
        pool ->
            pool.withConnectionTimeout(
                    Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                .withSocketTimeout(Config.getLong("users.socket.timeout")));
  }

  /**
   * Makes an API call to Users API using a user id and gets all the data associated to the user.
   * The model User will be returned. If an error occurs while parsing the response then null is
   * returned.
   *
   * @param context context
   * @param userId user id
   * @return User object
   * @throws ApiException (optional) if the api call fails
   */
  @Trace(dispatcher = true, nameTransaction = true)
  public User getById(final Context context, final Long userId) throws ApiException {
    final Headers headers = new Headers().add(REQUEST_ID, context.getRequestId());
    final URIBuilder url = buildUrl(userId);
    try {
      final Response response =
          RestUtils.newRestRequestBuilder(POOL_NAME).get(url.toString(), headers);

      METRIC_COLLECTOR.incrementCounter(
          REQUEST_OUT_COUNTER,
          DatadogUtils.getRequestOutCounterTags(
              HttpMethod.GET.name(), POOL_NAME, response.getStatus()));

      if (isSuccess(response.getStatus())) {
        LOGGER.info(
            LogUtils.getResponseLogWithResponseBody(
                context.getRequestId(),
                HttpMethod.GET.name(),
                POOL_NAME,
                URL,
                headers,
                LogUtils.convertQueryParam(url.getQueryParams()),
                response));
        return RestUtils.responseToObject(response, User.class);
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
      throw new ApiException(GsonWrapper.fromJson(RestUtils.getBody(response), ApiError.class));

    } catch (RestException e) {
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
      throw new ApiException(
          EXTERNAL_ERROR, "API call to users failed", HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
    }
  }

  /**
   * Builds the api call url using the user id
   *
   * @param userId preference id
   * @return a string with the url
   */
  public static URIBuilder buildUrl(final Long userId) {
    return new URIBuilder()
        .setScheme(Config.getString(Constants.USERS_URL_SCHEME))
        .setHost(Config.getString(Constants.USERS_URL_HOST))
        .setPath(String.format("%s/%s", URL, String.valueOf(userId)))
        .addParameter(CALLER_ID, String.valueOf(userId));
  }
}
