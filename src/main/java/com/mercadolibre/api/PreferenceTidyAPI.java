package com.mercadolibre.api;

import static com.mercadolibre.constants.Constants.API_CALL_PREFERENCE_TIDY_FAILED;
import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.X_REQUEST_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;
import static org.eclipse.jetty.http.HttpStatus.isSuccess;

import com.google.common.net.HttpHeaders;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.preference.PreferenceTidy;
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
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import com.newrelic.api.agent.Trace;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum PreferenceTidyAPI {
  INSTANCE;

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String POOL_NAME = "PreferenceTidyRead";
  private static final String URL = "/tidy/";

  static {
    RestUtils.registerPool(
        POOL_NAME,
        pool ->
            pool.withConnectionTimeout(
                    Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                .withSocketTimeout(Config.getLong("preference_tidy.socket.timeout"))
                .withRetryStrategy(
                    new SimpleRetryStrategy(
                        Config.getInt("preference_tidy.retries"),
                        Config.getLong("preference_tidy.retry.delay"))));
  }

  /**
   * Hace el API call a Preference Tidy para obtener el id de la preferencia
   *
   * @param context context
   * @param key key
   * @return preferenceTidy
   * @throws ApiException si falla el api call (status code is not 2xx)
   */
  @Trace(dispatcher = true, nameTransaction = true)
  public PreferenceTidy getPreferenceByKey(final Context context, final String key)
      throws ApiException {
    final Headers headers =
        new Headers()
            .add(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
            .add(X_REQUEST_ID, context.getRequestId());
    final String url = buildUrl(key);
    try {
      final Response response = RestUtils.newRestRequestBuilder(POOL_NAME).get(url, headers);

      METRIC_COLLECTOR.incrementCounter(
          REQUEST_OUT_COUNTER,
          DatadogUtils.getRequestOutCounterTags(
              HttpMethod.GET.name(), POOL_NAME, response.getStatus()));

      if (isSuccess(response.getStatus())) {
        LOGGER.info(
            LogUtils.getResponseLogWithoutResponseBody(
                context.getRequestId(),
                HttpMethod.GET.name(),
                POOL_NAME,
                URL,
                headers,
                key,
                response));

        return RestUtils.responseToObject(response, PreferenceTidy.class);
      } else {
        LOGGER.error(
            LogUtils.getResponseLogWithResponseBody(
                context.getRequestId(),
                HttpMethod.GET.name(),
                POOL_NAME,
                URL,
                headers,
                key,
                response));
      }
      throw new ApiException(GsonWrapper.fromJson(RestUtils.getBody(response), ApiError.class));

    } catch (final RestException e) {
      LogUtils.getExceptionLog(
          context.getRequestId(),
          HttpMethod.GET.name(),
          POOL_NAME,
          URL,
          headers,
          null,
          HttpStatus.SC_GATEWAY_TIMEOUT,
          e);

      METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
      throw new ApiException(
          EXTERNAL_ERROR, API_CALL_PREFERENCE_TIDY_FAILED, HttpStatus.SC_BAD_GATEWAY, e);
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
        .setPath(String.format(URL.concat(preferenceKey)))
        .toString();
  }
}
