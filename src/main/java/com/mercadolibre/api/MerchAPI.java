package com.mercadolibre.api;

import static com.mercadolibre.constants.Constants.ENABLED_HEADERS;
import static com.mercadolibre.constants.Constants.FLOW_NAME;
import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.constants.QueryParamsConstants.PLATFORM_VERSION;
import static com.mercadolibre.px.constants.CommonParametersNames.*;
import static com.mercadolibre.px.constants.HeadersConstants.X_REQUEST_ID;
import static com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils.METRIC_COLLECTOR;
import static org.apache.http.protocol.HTTP.USER_AGENT;
import static org.eclipse.jetty.http.HttpStatus.isSuccess;

import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.dto.congrats.merch.MerchResponse;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils;
import com.mercadolibre.px.monitoring.lib.utils.LogUtils;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.px.toolkit.utils.HeadersUtils;
import com.mercadolibre.px.toolkit.utils.MeliRestUtils;
import com.mercadolibre.px_config.Config;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.newrelic.api.agent.Trace;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum MerchAPI {
  INSTANCE;

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String URL = "/merch/middle-end/congrats/content";
  private static final String POOL_NAME = "MerchRead";
  private static final String PAYMENT_IDS = "paymentIds";
  private static final String CAMPAIGN_ID = "campaignId";
  private static final String ORIGIN = "origin";
  private static final String CHECKOUT_OFF = "checkout_off";
  private static final String LIMIT = "limit";
  private static final String DISCOUNTS_LIMIT = "6";

  static {
    MeliRestUtils.registerPool(
        POOL_NAME,
        pool ->
            pool.withConnectionTimeout(Config.getLong("merch.connection.timeout"))
                .withSocketTimeout(Config.getLong("merch.socket.timeout")));
  }

  /**
   * Makes an API call to Merch API using a user id, payments ids, site id, platform version and
   * gets cross selling and discounts.
   *
   * @param context context
   * @param congratsRequest request congrats
   * @return CompletableFutureEitherMerchResponseApiError
   */
  @Trace(async = true, dispatcher = true, nameTransaction = true)
  public CompletableFuture<Either<MerchResponse, ApiError>> getAsyncCrossSellingAndDiscount(
      final Context context, final CongratsRequest congratsRequest) {

    final Headers headers = new Headers().add(X_REQUEST_ID, context.getRequestId());

    Optional.ofNullable(context.getUserAgent())
        .map(userAgent -> headers.add(USER_AGENT, userAgent.toString()));

    final URIBuilder url = buildUrl(congratsRequest);

    try {
      final CompletableFuture<Response> completableFutureResponse =
          MeliRestUtils.newRestRequestBuilder(POOL_NAME)
              .asyncGet(url.toString(), headers, context.getMeliContext());

      return completableFutureResponse.thenApply(
          response -> {
            METRIC_COLLECTOR.incrementCounter(
                REQUEST_OUT_COUNTER,
                DatadogUtils.getRequestOutCounterTags(
                    HttpMethod.GET.name(), POOL_NAME, response.getStatus()));
            return buildResponse(context, headers, url, response);
          });
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
      return CompletableFuture.completedFuture(Either.alternative(ApiError.EXTERNAL_API));
    }
  }

  private Either<MerchResponse, ApiError> buildResponse(
      final Context context, final Headers headers, final URIBuilder url, final Response response) {
    if (isSuccess(response.getStatus())) {
      LOGGER.debug(
          LogUtils.getResponseLogWithoutResponseBody(
              context.getRequestId(),
              HttpMethod.GET.name(),
              POOL_NAME,
              URL,
              HeadersUtils.filter(headers, ENABLED_HEADERS),
              LogUtils.convertQueryParam(url.getQueryParams()),
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
    return MeliRestUtils.responseToEither(response, MerchResponse.class);
  }

  /**
   * Builds the api call url central discounts
   *
   * @param congratsRequest request congrats
   * @return a string with the url
   */
  public static URIBuilder buildUrl(final CongratsRequest congratsRequest) {
    final URIBuilder uriBuilder =
        new URIBuilder()
            .setScheme(Config.getString("merch.url.scheme"))
            .setHost(Config.getString("merch.url.host"))
            .setPath(URL)
            .addParameter(CALLER_ID, congratsRequest.getUserId())
            .addParameter(CLIENT_ID, congratsRequest.getClientId())
            .addParameter(CALLER_SITE_ID, congratsRequest.getSiteId())
            .addParameter(PAYMENT_IDS, congratsRequest.getPaymentIds())
            .addParameter(LIMIT, DISCOUNTS_LIMIT)
            .addParameter(ORIGIN, CHECKOUT_OFF)
            .addParameter(FLOW_NAME, congratsRequest.getFlowName());

    if (null != congratsRequest.getUserAgent().getVersion().getVersionName()) {
      uriBuilder.addParameter(
          PLATFORM_VERSION, congratsRequest.getUserAgent().getVersion().getVersionName());
    }
    if (StringUtils.isNotBlank(congratsRequest.getCampaignId())) {
      uriBuilder.addParameter(CAMPAIGN_ID, congratsRequest.getCampaignId());
    }

    return uriBuilder;
  }

  public static Optional<MerchResponse> getMerchResponseFromFuture(
      final Context context, final CompletableFuture<Either<MerchResponse, ApiError>> future) {
    try {
      if (future.get().isValuePresent()) {
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
              new Headers().add(X_REQUEST_ID, context.getRequestId()),
              null,
              HttpStatus.SC_GATEWAY_TIMEOUT,
              e));
      METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
      return Optional.empty();
    }
  }
}
