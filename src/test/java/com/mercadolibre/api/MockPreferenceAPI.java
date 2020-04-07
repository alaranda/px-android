package com.mercadolibre.api;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;

public class MockPreferenceAPI {

  public static void getById(final String id, final int statusCode, final String body) {
    MockResponse.builder()
        .withURL(PreferenceAPI.buildUrl(id).toString())
        .withMethod(HttpMethod.GET)
        .withStatusCode(statusCode)
        .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
        .withResponseHeader("Cache-Control", "max-age=0")
        .withResponseBody(body)
        .build();
  }
}
