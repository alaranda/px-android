package com.mercadolibre.api;

import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;

import static com.mercadolibre.api.MerchAPI.buildUrl;

public class MockAdServerAPI {

  public static void getAd(
      final CongratsRequest congratsRequest, final int statusCode, final String body) {
    MockResponse.builder()
        .withURL(buildUrl(congratsRequest).toString())
        .withMethod(HttpMethod.GET)
        .withStatusCode(statusCode)
        .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
        .withResponseHeader("Cache-Control", "max-age=0")
        .withResponseBody(body)
        .build();
  }
}
