package com.mercadolibre.api;

import static com.mercadolibre.api.FraudApi.getPath;
import static com.mercadolibre.px.constants.HeadersConstants.CLIENT_ID;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.HttpMethod;

public class MockFraudApi {

  public static void resetCapEsc(final String cardId, final String clientId, final int statusCode) {
    MockResponse.builder()
        .withURL(getPath(cardId).toString())
        .withMethod(HttpMethod.DELETE)
        .withRequestHeader(new Header(CLIENT_ID, clientId))
        .withStatusCode(statusCode)
        .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
        .withResponseHeader("Cache-Control", "max-age=0")
        .withResponseBody("")
        .build();
  }
}
