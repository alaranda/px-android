package com.mercadolibre.api;

import static com.mercadolibre.px.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.restclient.http.ContentType.HEADER_NAME;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.HttpMethod;

public class MockPaymentMethodSearchAPI {

  public static void getPaymentMethodsAsync(
      final String siteId,
      final String marketplace,
      final String paymentMethodId,
      final int statusCode,
      final String body) {
    MockResponse.builder()
        .withURL(PaymentMethodsSearchApi.buildUrl(siteId, marketplace, paymentMethodId).toString())
        .withMethod(HttpMethod.GET)
        .withStatusCode(statusCode)
        .withResponseHeader(HEADER_NAME, APPLICATION_JSON.toString())
        .withResponseBody(body)
        .withRequestHeader(REQUEST_ID, "test-request-id")
        .build();
  }
}
