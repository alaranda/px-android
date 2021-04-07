package com.mercadolibre.api;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;

public class MockRiskApi {

  public static void getRisk(final long riskExcecutionId, final int statusCode, final String body) {
    MockResponse.Builder builder =
        MockResponse.builder()
            .withURL(RiskApi.getPath(riskExcecutionId).toString())
            .withMethod(HttpMethod.GET)
            .withStatusCode(statusCode)
            .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
            .withResponseHeader("Cache-Control", "max-age=0")
            .withResponseBody(body);

    builder.build();
  }
}
