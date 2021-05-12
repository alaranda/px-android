package com.mercadolibre.api;

import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.constants.CommonParametersNames.ACCESS_TOKEN;
import static com.mercadolibre.px.constants.CommonParametersNames.API_VERSION;
import static com.mercadolibre.px.constants.CommonParametersNames.PUBLIC_KEY;
import static com.mercadolibre.px.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.constants.HeadersConstants.X_REQUEST_ID;
import static com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils.METRIC_COLLECTOR;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.instructions.InstructionsResponse;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils;
import com.mercadolibre.px.monitoring.lib.utils.LogUtils;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.px.toolkit.utils.MeliRestUtils;
import com.mercadolibre.px_config.Config;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InstructionsApi {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String POOL_NAME = "InstructionsRead";

  static {
    MeliRestUtils.registerPool(
        POOL_NAME,
        pool ->
            pool.withConnectionTimeout(
                    Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                .withSocketTimeout(Config.getLong("instructions.socket.timeout")));
  }

  static URIBuilder getPath(
      final String paymentId, final String accessToken, final String version) {

    return new URIBuilder()
        .setScheme(Config.getString("api.base.url.scheme"))
        .setHost(Config.getString("api.base.mp.url.host"))
        .setPath(String.format(Config.getString("instructions.url"), paymentId))
        .setParameter(ACCESS_TOKEN, accessToken)
        .setParameter(PUBLIC_KEY, "test")
        .setParameter(API_VERSION, version);
  }

  public Either<InstructionsResponse, ApiError> getInstructions(
      final Context context, final String paymentId, final String accessToken) {
    final Headers headers = new Headers().add(X_REQUEST_ID, context.getRequestId());
    final String version = context.getUserAgent().getVersion().getVersionName();
    final URIBuilder url = getPath(paymentId, accessToken, version);

    try {
      final Response response =
          MeliRestUtils.newRestRequestBuilder(POOL_NAME)
              .get(url.toString(), headers, context.getMeliContext());
      METRIC_COLLECTOR.incrementCounter(
          REQUEST_OUT_COUNTER,
          DatadogUtils.getRequestOutCounterTags(
              HttpMethod.GET.name(), POOL_NAME, response.getStatus()));

      if (MeliRestUtils.isResponseSuccessful(response)) {
        return MeliRestUtils.responseToEither(response, InstructionsResponse.class);
      }

      LOGGER.error(
          LogUtils.getResponseLogWithResponseBody(
              context.getRequestId(),
              HttpMethod.GET.name(),
              POOL_NAME,
              getPath(paymentId, accessToken, version).toString(),
              headers,
              LogUtils.convertQueryParam(url.getQueryParams()),
              response));
      METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
      return Either.alternative(
          new ApiError(EXTERNAL_ERROR, "API call to instructions failed", response.getStatus()));
    } catch (final RestException e) {
      LOGGER.error(
          LogUtils.getExceptionLog(
              context.getRequestId(),
              HttpMethod.GET.name(),
              POOL_NAME,
              getPath(paymentId, accessToken, version).toString(),
              headers,
              LogUtils.convertQueryParam(url.getQueryParams()),
              HttpStatus.SC_GATEWAY_TIMEOUT,
              e));

      METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
      return Either.alternative(
          new ApiError(
              EXTERNAL_ERROR, "API call to instructions failed", HttpStatus.SC_GATEWAY_TIMEOUT));
    }
  }
}
