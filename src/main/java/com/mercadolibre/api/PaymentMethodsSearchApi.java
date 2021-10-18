package com.mercadolibre.api;

import static com.mercadolibre.constants.Constants.ENABLED_HEADERS;
import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.constants.CommonParametersNames.MARKETPLACE;
import static com.mercadolibre.px.constants.CommonParametersNames.SITE_ID;
import static com.mercadolibre.px.constants.CommonParametersNames.STATUS;
import static com.mercadolibre.px.constants.ConstantsNames.STATUS_ACTIVE;
import static com.mercadolibre.px.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils.METRIC_COLLECTOR;
import static com.mercadolibre.px.toolkit.utils.MeliRestUtils.newRestRequestBuilder;
import static com.mercadolibre.px.toolkit.utils.MeliRestUtils.registerPool;
import static java.util.Objects.requireNonNull;
import static org.eclipse.jetty.http.HttpStatus.isSuccess;

import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.installments.PaymentMethod;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils;
import com.mercadolibre.px.monitoring.lib.log.MonitoringUtils;
import com.mercadolibre.px.monitoring.lib.utils.LogUtils;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.px.toolkit.utils.HeadersUtils;
import com.mercadolibre.px.toolkit.utils.MeliRestUtils;
import com.mercadolibre.px_config.Config;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaymentMethodsSearchApi {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String POOL_NAME = "payment_methods_search";
  private static final String URL = "/v1/payment_methods/search";

  private static final String QUERY_PARAM_ID = "id";

  static {
    registerPool(
        POOL_NAME,
        pool ->
            pool.withRetryStrategy(
                    new SimpleRetryStrategy(
                        Config.getInt("payment_methods_search.retries"),
                        Config.getLong("payment_methods_search.delay")))
                .withSocketTimeout(Config.getLong("payment_methods_search.socket.timeout")));
  }

  public CompletableFuture<Either<PaymentMethodsSearchDTO, ApiError>> getPaymentMethodsAsync(
      final Context context, final String siteId, final String marketplace, final String id)
      throws ApiException {
    final Headers headers = HeadersUtils.getHeadersWithCallerScopes(context.getRequestId());
    final URIBuilder uri = buildUrl(siteId, marketplace, id);

    try {
      final CompletableFuture<Response> completableFuture =
          newRestRequestBuilder(POOL_NAME)
              .asyncGet(uri.toString(), headers, context.getMeliContext());

      return completableFuture.thenApply(
          response -> {
            METRIC_COLLECTOR.incrementCounter(
                REQUEST_OUT_COUNTER,
                DatadogUtils.getRequestOutCounterTags(
                    HttpMethod.GET.name(), POOL_NAME, response.getStatus()));
            return buildResponse(context, headers, uri, response, HttpMethod.GET.name());
          });

    } catch (final RestException e) {
      LOGGER.error(
          LogUtils.getExceptionLog(
              context.getRequestId(),
              HttpMethod.GET.name(),
              POOL_NAME,
              uri.toString(),
              headers,
              LogUtils.convertQueryParam(uri.getQueryParams()),
              HttpStatus.SC_GATEWAY_TIMEOUT,
              e));
      METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
      throw new ApiException(
          EXTERNAL_ERROR,
          "API call to payment methods search failed",
          HttpStatus.SC_GATEWAY_TIMEOUT);
    }
  }

  public Optional<PaymentMethodsSearchDTO> getPaymentMethodsFromFuture(
      final Context context,
      final CompletableFuture<Either<PaymentMethodsSearchDTO, ApiError>> future)
      throws ApiException {
    try {
      if (null != future && future.get().isValuePresent()) {
        return Optional.ofNullable(future.get().getValue());
      } else {
        return Optional.empty();
      }
    } catch (InterruptedException | ExecutionException e) {
      MonitoringUtils.logException(
          context, "parse_async", POOL_NAME, "", Collections.emptyList(), null, e);

      throw new ApiException(
          String.format("pool[%s]_external_api_error", POOL_NAME),
          String.format(
              "Error produced trying to parse async response from: [%s], error[%s]",
              POOL_NAME, e.getMessage()),
          HttpStatus.SC_GATEWAY_TIMEOUT);
    }
  }

  static URIBuilder buildUrl(
      final String siteId, final String marketplace, final String paymentMethodId) {

    URIBuilder uri =
        new URIBuilder()
            .setScheme(Config.getString("payment_methods_search.url.scheme"))
            .setHost(Config.getString("payment_methods_search.url.host"))
            .setPath(URL)
            .addParameter(MARKETPLACE, marketplace)
            .addParameter(STATUS, STATUS_ACTIVE)
            .addParameter(SITE_ID, requireNonNull(siteId));

    if (null != paymentMethodId) {
      uri.addParameter(QUERY_PARAM_ID, paymentMethodId);
    }
    return uri;
  }

  private Either<PaymentMethodsSearchDTO, ApiError> buildResponse(
      final Context context,
      final Headers headers,
      final URIBuilder url,
      final Response response,
      final String httpMethod) {
    if (isSuccess(response.getStatus())) {
      LOGGER.debug(
          LogUtils.getResponseLogWithoutResponseBody(
              context.getRequestId(),
              httpMethod,
              POOL_NAME,
              url.toString(),
              HeadersUtils.filter(headers, ENABLED_HEADERS),
              LogUtils.convertQueryParam(url.getQueryParams()),
              response));
    } else {
      LOGGER.error(
          LogUtils.getResponseLogWithResponseBody(
              context.getRequestId(),
              httpMethod,
              POOL_NAME,
              url.toString(),
              headers,
              LogUtils.convertQueryParam(url.getQueryParams()),
              response));
    }
    return MeliRestUtils.responseToEither(response, PaymentMethodsSearchDTO.class);
  }

  public static class PaymentMethodsSearchDTO {
    @SuppressWarnings("unused")
    private Collection<PaymentMethod> results;

    public Collection<PaymentMethod> getResults() {
      return results;
    }
  }
}
