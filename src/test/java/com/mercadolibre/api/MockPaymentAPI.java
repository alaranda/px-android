package com.mercadolibre.api;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.HttpMethod;

public class MockPaymentAPI {

  public static void doPayment(
      final Long callerId,
      final Long clientId,
      final int statusCode,
      final String body,
      final Header... headers) {
    MockResponse.Builder builder =
        MockResponse.builder()
            .withURL(PaymentAPI.buildUrl(callerId, clientId).toString())
            .withMethod(HttpMethod.POST)
            .withStatusCode(statusCode)
            .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
            .withResponseHeader("Cache-Control", "max-age=0")
            .withResponseBody(body);

    for (Header h : headers) {
      builder.withRequestHeader(h);
    }

    builder.build();
  }

  public static void doPaymentFail(final Long callerId, final Long clientId) {
    MockResponse.builder()
        .withURL(PaymentAPI.buildUrl(callerId, clientId).toString())
        .withMethod(HttpMethod.POST)
        .shouldFail()
        .build();
  }

  public static void getPayment(final String paymentId, final int statusCode, final String body) {
    MockResponse.Builder builder =
        MockResponse.builder()
            .withURL(PaymentAPI.buildGetPaymentUrl(paymentId).toString())
            .withMethod(HttpMethod.GET)
            .withStatusCode(statusCode)
            .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
            .withResponseHeader("Cache-Control", "max-age=0")
            .withResponseBody(body);

    builder.build();
  }
}
