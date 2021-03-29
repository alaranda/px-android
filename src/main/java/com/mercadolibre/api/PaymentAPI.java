package com.mercadolibre.api;

import static com.mercadolibre.constants.Constants.API_CALL_PAYMENTS_FAILED;
import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils.METRIC_COLLECTOR;
import static org.eclipse.jetty.http.HttpStatus.isSuccess;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentBody;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils;
import com.mercadolibre.px.monitoring.lib.utils.LogUtils;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.px.toolkit.utils.MeliRestUtils;
import com.mercadolibre.px_config.Config;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.newrelic.api.agent.Trace;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum PaymentAPI {
  INSTANCE;

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String URL = "/v1/payments";
  private static final String POOL_NAME = "PaymentsWrite";
  private static final String POOL_NAME_READ = "PaymentsRead";

  static {
    MeliRestUtils.registerPool(
        POOL_NAME,
        pool ->
            pool.withConnectionTimeout(
                    Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                .withSocketTimeout(Config.getLong("payment.socket.timeout")));
  }

  /**
   * Apicall to payments
   *
   * @param context context object
   * @param callerId caller id
   * @param clientId client id
   * @param body body
   * @param headers headers
   * @return EitherPaymentApiError
   * @throws ApiException (optional) if the api call fail
   */
  @Trace(dispatcher = true, nameTransaction = true)
  public Either<Payment, ApiError> doPayment(
      final Context context,
      final Long callerId,
      final Long clientId,
      final PaymentBody body,
      final Headers headers)
      throws ApiException {
    final URIBuilder url = buildUrl(callerId, clientId);

    try {
      final Response response =
          MeliRestUtils.newRestRequestBuilder(POOL_NAME)
              .asyncPost(
                  url.toString(),
                  headers,
                  GsonWrapper.toJson(body).getBytes(StandardCharsets.UTF_8),
                  context.getMeliContext())
              .get();

      METRIC_COLLECTOR.incrementCounter(
          REQUEST_OUT_COUNTER,
          DatadogUtils.getRequestOutCounterTags(
              HttpMethod.POST.name(), POOL_NAME, response.getStatus()));

      return buildResponse(context, headers, url, response, HttpMethod.POST.name(), POOL_NAME);
    } catch (final RestException | InterruptedException | ExecutionException e) {
      LOGGER.error(
          LogUtils.getExceptionLog(
              context.getRequestId(),
              HttpMethod.POST.name(),
              POOL_NAME,
              URL,
              headers,
              LogUtils.convertQueryParam(url.getQueryParams()),
              HttpStatus.SC_GATEWAY_TIMEOUT,
              e));
      METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
      throw new ApiException(EXTERNAL_ERROR, API_CALL_PAYMENTS_FAILED, HttpStatus.SC_BAD_GATEWAY);
    }
  }

  /**
   * Builds the api call url using the preference id
   *
   * @param callerId caller id
   * @param callerId client id
   * @return a string with the url
   */
  static URIBuilder buildUrl(final Long callerId, final Long clientId) {
    return new URIBuilder()
        .setScheme(Config.getString("payment.url.scheme"))
        .setHost(Config.getString("payment.url.host"))
        .setPath(URL)
        .addParameter("caller.id", String.valueOf(callerId))
        .addParameter("client.id", String.valueOf(clientId));
  }

  /**
   * Apicall to payments
   *
   * @param context context object
   * @param paymentId payment id
   * @return CompletableFutureEitherPaymentApiError
   * @throws ApiException (optional) if the api call fail
   */
  @Trace(async = true, dispatcher = true, nameTransaction = true)
  public CompletableFuture<Either<Payment, ApiError>> getAsyncPayment(
      final Context context, final String paymentId) throws ApiException {

    final Headers headers = new Headers().add(REQUEST_ID, context.getRequestId());
    final URIBuilder url = buildGetPaymentUrl(paymentId);

    try {
      final CompletableFuture<Response> completableFuture =
          MeliRestUtils.newRestRequestBuilder(POOL_NAME)
              .asyncGet(url.toString(), headers, context.getMeliContext());

      return completableFuture.thenApply(
          response -> {
            METRIC_COLLECTOR.incrementCounter(
                REQUEST_OUT_COUNTER,
                DatadogUtils.getRequestOutCounterTags(
                    HttpMethod.GET.name(), POOL_NAME_READ, response.getStatus()));
            return buildResponse(
                context, headers, url, response, HttpMethod.GET.name(), POOL_NAME_READ);
          });
    } catch (final RestException e) {
      LOGGER.error(
          LogUtils.getExceptionLog(
              context.getRequestId(),
              HttpMethod.GET.name(),
              POOL_NAME_READ,
              url.toString(),
              headers,
              LogUtils.convertQueryParam(url.getQueryParams()),
              HttpStatus.SC_GATEWAY_TIMEOUT,
              e));
      METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME_READ);
      throw new ApiException(EXTERNAL_ERROR, API_CALL_PAYMENTS_FAILED, HttpStatus.SC_BAD_GATEWAY);
    }
  }

  /**
   * Builds the api call url using the preference id
   *
   * @param paymentId payment id
   * @return a string with the url
   */
  static URIBuilder buildGetPaymentUrl(final String paymentId) {
    return new URIBuilder()
        .setScheme(Config.getString("payment.url.scheme"))
        .setHost(Config.getString("payment.url.host"))
        .setPath(URL.concat("/" + paymentId))
        .addParameter("caller.scopes", "payments,admin");
  }

  private Either<Payment, ApiError> buildResponse(
      final Context context,
      final Headers headers,
      final URIBuilder url,
      final Response response,
      final String httpMethod,
      final String poolName) {
    if (isSuccess(response.getStatus())) {
      LOGGER.info(
          LogUtils.getResponseLogWithoutResponseBody(
              context.getRequestId(),
              httpMethod,
              poolName,
              url.toString(),
              headers,
              LogUtils.convertQueryParam(url.getQueryParams()),
              response));
    } else {
      LOGGER.error(
          LogUtils.getResponseLogWithResponseBody(
              context.getRequestId(),
              httpMethod,
              poolName,
              url.toString(),
              headers,
              LogUtils.convertQueryParam(url.getQueryParams()),
              response));
    }
    return MeliRestUtils.responseToEither(response, Payment.class);
  }

  public Optional<Payment> getPaymentFromFuture(
      final Context context, final CompletableFuture<Either<Payment, ApiError>> future) {
    try {
      if (future != null && future.get().isValuePresent()) {
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
