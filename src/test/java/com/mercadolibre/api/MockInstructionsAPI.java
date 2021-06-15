package com.mercadolibre.api;

import com.mercadolibre.px.constants.HeadersConstants;
import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;
import java.util.UUID;

public class MockInstructionsAPI {
  public static void getInstructions(
      String paymentId,
      String accessToken,
      String publicKey,
      String paymentTypeId,
      String bodyResponse,
      int httpStatus) {
    MockResponse.builder()
        .withURL(
            InstructionsApi.getPath(paymentId, accessToken, publicKey, paymentTypeId).toString())
        .withMethod(HttpMethod.GET)
        .withRequestHeader(HeadersConstants.X_REQUEST_ID, UUID.randomUUID().toString())
        .withStatusCode(httpStatus)
        .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
        .withResponseBody(bodyResponse)
        .build();
  }
}
