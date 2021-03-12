package com.mercadolibre.api;

import static com.mercadolibre.api.CardHolderAuthenticationAPI.getPath;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;

/** Mock Card Holder Authentication API */
public class MockCHAAPI {

  public static void authenticateCard(
      final String cardToken, final String body, final int statusCode) {
    MockResponse.builder()
        .withURL(getPath(cardToken).toString())
        .withMethod(HttpMethod.POST)
        .withStatusCode(statusCode)
        .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
        .withResponseHeader("Cache-Control", "max-age=0")
        .withResponseBody(body)
        .build();
  }
}
