package com.mercadolibre.api;

import static com.mercadolibre.constants.Constants.API_CALL_PREFERENCE_FAILED;
import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.X_CALLER_SCOPES;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.X_REQUEST_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;
import static org.eclipse.jetty.http.HttpStatus.isSuccess;

import com.google.common.net.HttpHeaders;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.toolkit.config.Config;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum PreferenceAPI {
  INSTANCE;

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String POOL_NAME = "PreferencesRead";
  private static final String URL = "/checkout/preferences";

  static {
    RestUtils.registerPool(
        POOL_NAME,
        pool ->
            pool.withConnectionTimeout(
                    Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                .withSocketTimeout(Config.getLong("preference.socket.timeout"))
                .withRetryStrategy(
                    new SimpleRetryStrategy(
                        Config.getInt("preference.retries"),
                        Config.getLong("preference.retry.delay"))));
  }

  /**
   * Hace el API call a Preference para obtener la preferencia
   *
   * @param context context object
   * @param preferenceId id de la preferencia
   * @return CompletableFutureEitherPreferenceApiError
   * @throws ApiException si falla el api call (status code is not 2xx)
   */
  @Trace(async = true, dispatcher = true, nameTransaction = true)
  public CompletableFuture<Either<Preference, ApiError>> geAsynctPreference(
      final Context context, final String preferenceId) throws ApiException {
    final Headers headers =
        new Headers()
            .add(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
            .add(X_CALLER_SCOPES, "admin")
            .add(X_REQUEST_ID, context.getRequestId());
    final URIBuilder url = buildUrl(preferenceId);
    try {
      final CompletableFuture<Response> completableFutureResponse =
          RestUtils.newRestRequestBuilder(POOL_NAME).asyncGet(url.toString(), headers);

      return completableFutureResponse.thenApply(
          response -> {
            METRIC_COLLECTOR.incrementCounter(
                REQUEST_OUT_COUNTER,
                DatadogUtils.getRequestOutCounterTags(
                    HttpMethod.GET.name(), POOL_NAME, response.getStatus()));
            return buildResponse(context, headers, url, response, preferenceId);
          });

    } catch (final RestException e) {
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
          EXTERNAL_ERROR, API_CALL_PREFERENCE_FAILED, HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
    }
  }

  private Either<Preference, ApiError> buildResponse(
      final Context context,
      final Headers headers,
      final URIBuilder url,
      final Response response,
      final String preferenceId) {
    if (isSuccess(response.getStatus())) {
      LOGGER.info(
          LogUtils.getResponseLogWithoutResponseBody(
              context.getRequestId(),
              HttpMethod.GET.name(),
              POOL_NAME,
              URL,
              headers,
              preferenceId,
              response));
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
    return RestUtils.responseToEither(response, Preference.class);
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

  public static Optional<Preference> getPreferenceFromFuture(
      final Context context, final CompletableFuture<Either<Preference, ApiError>> future) {
    try {
      if (null != future && future.get().isValuePresent()) {
        return Optional.ofNullable(future.get().getValue());
      } else {
        return Optional.empty();
      }
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.error(
          LogUtils.getExceptionLog(
              context.getRequestId(),
              HttpMethod.GET.name(),
              POOL_NAME,
              URL,
              new Headers().add(REQUEST_ID, context.getRequestId()),
              null,
              HttpStatus.SC_GATEWAY_TIMEOUT,
              e));
      METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
      return Optional.empty();
    }
  }
}
