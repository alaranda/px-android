package com.mercadolibre.api;

import com.mercadolibre.dto.congrats.Banner;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

public enum AdServerAPI {
  INSTANCE;

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String URL = "/internal/advertising/adserver/ads";


  private static final String POOL_NAME = "AdServerRead";



  //headers en duro (momentaneo)
  private static final String XD2ID = "x-d2id";
  private static final String XCALLERID = "x-caller-id";



  //parametros en duro (momentaneo)
  private static final String USER_ID = "user.id";
  private static final String PLACEMENT = "placement";
  private static final String HASWEBP = "has_webp";
  private static final String BUSINESS = "business";
  private static final String PLATFORM = "platform";
  private static final String ISBASE64 = "is_base_64";




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
  public CompletableFuture<Either<Banner, ApiError>> getAd(
      final Context context, final CongratsRequest congratsRequest){

    // en duro mientras
    final Headers headers = new Headers().add(XCALLERID, "1038487457");
    headers.add(XD2ID,"19886b76-dbdf-4a81-9a19-396a23044be7");



    headers.add(USER_AGENT, "MercadoLibre-Android/10.173.3 (x86_64; Android 0.0.0)");

    final URIBuilder url = buildUrl(congratsRequest);

    System.out.println(Config.getString("merch.url.scheme"));

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


  private Either<Banner, ApiError> buildResponse(
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
    return MeliRestUtils.responseToEither(response, Banner.class);
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
            .setScheme(Config.getString("adserv.url.scheme"))
            .setHost(Config.getString("adserv.url.host"))
            .setPath(URL)
            .addParameter(USER_ID, "242307420")
            .addParameter(CALLER_SITE_ID, "MLA")
            .addParameter(PLACEMENT, "home/top_home_banner")
            .addParameter(BUSINESS, "ml")
            .addParameter(PLATFORM, "android")
            .addParameter(HASWEBP, "true")
            .addParameter(ISBASE64, "true");

    return uriBuilder;
  }
  public static Optional<Banner> getBannerFromFuture(
          final Context context, final CompletableFuture<Either<Banner, ApiError>> future) {
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
