package com.mercadolibre.api;

import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils.METRIC_COLLECTOR;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.cha.CardHolder;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.payment.ProcessingMode;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils;
import com.mercadolibre.px.monitoring.lib.utils.LogUtils;
import com.mercadolibre.px.toolkit.gson.*;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.px.toolkit.utils.MeliRestUtils;
import com.mercadolibre.px_config.Config;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.OptionalInt;
import java.util.OptionalLong;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Card Holder Authentication API */
public class CardHolderAuthenticationAPI {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String POOL_NAME = "CHARead";
  private static final String CARD_TOKEN_HIDDEN = "CARD-****-TOKEN";

  static {
    MeliRestUtils.registerPool(
        POOL_NAME,
        pool ->
            pool.withConnectionTimeout(
                    Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                .withSocketTimeout(Config.getLong("cha.socket.timeout")));
  }

  static URIBuilder getPath(final String cardToken) {

    return new URIBuilder()
        .setScheme(Config.getString("cha.url.scheme"))
        .setHost(Config.getString("cha.url.host"))
        .setPath(Config.getString("cha.v1.uri") + '/' + cardToken);
  }

  static String getBody(final CardHolder request) {
    final GsonBuilder gsonBuilder = new GsonBuilder();

    return gsonBuilder
        .registerTypeAdapter(Either.class, new EitherAdapter())
        .registerTypeAdapter(OptionalLong.class, new OptionalLongAdapter())
        .registerTypeAdapterFactory(OptionalAdapter.FACTORY)
        .registerTypeAdapter(OptionalInt.class, new OptionalIntAdapter())
        .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
        .registerTypeAdapter(ProcessingMode.class, new UppercaseEnumAdapterDeserializer())
        .registerTypeAdapter(ProcessingMode.class, new EnumToStringAdapterSerializer())
        .registerTypeAdapter(Site.class, new UppercaseEnumAdapterDeserializer())
        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        .create()
        .toJson(request);
  }

  public Object authenticateCard(
      final Context context, final String cardToken, final CardHolder request) throws ApiException {
    final Headers headers = new Headers().add(REQUEST_ID, context.getRequestId());
    final URIBuilder url = getPath(cardToken);
    final String body = getBody(request);

    try {
      final Response response =
          MeliRestUtils.newRestRequestBuilder(POOL_NAME)
              .post(
                  url.toString(),
                  headers,
                  body.getBytes(StandardCharsets.UTF_8),
                  context.getMeliContext());
      METRIC_COLLECTOR.incrementCounter(
          REQUEST_OUT_COUNTER,
          DatadogUtils.getRequestOutCounterTags(
              HttpMethod.POST.name(), POOL_NAME, response.getStatus()));

      if (MeliRestUtils.isResponseSuccessful(response)) {
        // TODO: modelado del response
        return MeliRestUtils.responseToObject(response, Object.class);
      }

      LOGGER.error(
          LogUtils.getResponseLogWithResponseBody(
              context.getRequestId(),
              HttpMethod.POST.name(),
              POOL_NAME,
              getPath(CARD_TOKEN_HIDDEN).toString(),
              headers,
              LogUtils.convertQueryParam(url.getQueryParams()),
              response));

      throw new ApiException(EXTERNAL_ERROR, "API call to CHA failed", response.getStatus());
    } catch (final RestException e) {
      LOGGER.error(
          LogUtils.getExceptionLog(
              context.getRequestId(),
              HttpMethod.POST.name(),
              POOL_NAME,
              getPath(CARD_TOKEN_HIDDEN).toString(),
              headers,
              LogUtils.convertQueryParam(url.getQueryParams()),
              HttpStatus.SC_GATEWAY_TIMEOUT,
              e));

      METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
      throw new ApiException(
          EXTERNAL_ERROR,
          "API call to authenticate card holder failed",
          HttpStatus.SC_GATEWAY_TIMEOUT);
    }
  }
}
