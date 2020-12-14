package com.mercadolibre.api;

import static com.mercadolibre.constants.Constants.API_CALL_TED_FAILED;
import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;
import static org.eclipse.jetty.http.HttpStatus.isSuccess;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.Ted;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.config.Config;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.px.toolkit.utils.RestUtils;
import com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.HttpMethod;
import java.util.concurrent.CompletableFuture;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum TedAPI {
  INSTANCE;

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String URL = "/ted/accounts";
  private static final String POOL_NAME = "TedRead";

  static {
    RestUtils.registerPool(
        POOL_NAME,
        pool ->
            pool.withConnectionTimeout(
                    Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                .withSocketTimeout(Config.getLong("ted.socket.timeout")));
  }

  static URIBuilder buildUrl(final Long userId) {
    return new URIBuilder()
        .setScheme(Config.getString("ted.url.scheme"))
        .setHost(Config.getString("ted.url.host"))
        .setPath(URL)
        .addParameter("user_id", String.valueOf(userId));
  }

  public CompletableFuture<Either<Ted, ApiError>> getAsyncTed(
      final Context context, final Long userId) throws ApiException {
    final URIBuilder url = buildUrl(userId);
    try {
      final CompletableFuture<Response> completableFutureResponse =
          RestUtils.newRestRequestBuilder(POOL_NAME).asyncGet(url.toString());

      return completableFutureResponse.thenApply(
          response -> {
            METRIC_COLLECTOR.incrementCounter(
                REQUEST_OUT_COUNTER,
                DatadogUtils.getRequestOutCounterTags(
                    HttpMethod.GET.name(), POOL_NAME, response.getStatus()));
            return buildResponse(context, url, response, userId);
          });
    } catch (RestException e) {
      LOGGER.error(
          LogUtils.getExceptionLog(
              context.getRequestId(),
              HttpMethod.GET.name(),
              POOL_NAME,
              URL,
              null,
              LogUtils.convertQueryParam(url.getQueryParams()),
              HttpStatus.SC_GATEWAY_TIMEOUT,
              e));
      METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
      throw new ApiException(
          EXTERNAL_ERROR, API_CALL_TED_FAILED, HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
    }
  }

  private Either<Ted, ApiError> buildResponse(
      final Context context, final URIBuilder url, final Response response, final Long userId) {
    if (isSuccess(response.getStatus())) {
      LOGGER.info(
          LogUtils.getResponseLogWithoutResponseBody(
              context.getRequestId(),
              HttpMethod.GET.name(),
              POOL_NAME,
              URL,
              null,
              String.valueOf(userId),
              response));
    } else {
      LOGGER.error(
          LogUtils.getResponseLogWithResponseBody(
              context.getRequestId(),
              HttpMethod.GET.name(),
              POOL_NAME,
              URL,
              null,
              LogUtils.convertQueryParam(url.getQueryParams()),
              response));
    }
    return RestUtils.responseToEither(response, Ted.class);
  }
}
