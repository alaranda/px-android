package com.mercadolibre.api;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;

public class MockKycVaultDao {

  public static void getSensitiveUserData(final int statusCode, final String body) {
    MockResponse.builder()
        .withURL("http://api.internal.ml.com/kyc/graphql")
        .withMethod(HttpMethod.POST)
        .withStatusCode(statusCode)
        .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
        .withResponseHeader("Cache-Control", "max-age=0")
        .withResponseBody(body)
        .build();
  }
}
