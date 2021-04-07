package com.mercadolibre.api;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;

public class MockMerchantOrderAPI {

  public static void createMerchantOrder(
      final String collectorId, final int statusCode, final String body) {
    MockResponse.builder()
        .withURL(MerchantOrderAPI.buildUrl(collectorId).toString())
        .withMethod(HttpMethod.POST)
        .withStatusCode(statusCode)
        .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
        .withResponseHeader("Cache-Control", "max-age=0")
        .withResponseBody(body)
        .build();
  }
}
