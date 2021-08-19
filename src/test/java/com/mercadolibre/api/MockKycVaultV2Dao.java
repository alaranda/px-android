package com.mercadolibre.api;

import com.mercadolibre.px_config.Config;
import com.mercadolibre.restclient.MockInterceptor;
import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;

public class MockKycVaultV2Dao {

  public static void getAsync(final int statusCode, final String body) {
    getAsync(statusCode, body, null);
  }

  public static void getAsync(
      final int statusCode, final String body, final MockInterceptor interceptor) {
    String scheme = Config.getString("api.base.url.scheme");
    String host = Config.getString("api.base.url.host");

    MockResponse.builder()
        .withURL(scheme + "://" + host + "/v2/kyc/vault")
        .withMethod(HttpMethod.POST)
        .withStatusCode(statusCode)
        .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
        .withResponseHeader("Cache-Control", "max-age=0")
        .withResponseBody(body)
        .withMockInterceptor(interceptor)
        .build();
  }
}
