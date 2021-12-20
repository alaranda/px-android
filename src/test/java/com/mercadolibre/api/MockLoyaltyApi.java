package com.mercadolibre.api;

import static com.mercadolibre.api.LoyaltyApi.buildUrlActionPayment;
import static com.mercadolibre.api.LoyaltyApi.buildUrlActionPurchase;

import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;

public class MockLoyaltyApi {

  public static void getAsyncPointsFromPayments(
      final CongratsRequest congratsRequest, final int statusCode, final String body) {
    MockResponse.builder()
        .withURL(buildUrlActionPayment(congratsRequest, congratsRequest.getPaymentIds()).toString())
        .withMethod(HttpMethod.GET)
        .withStatusCode(statusCode)
        .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
        .withResponseHeader("Cache-Control", "max-age=0")
        .withResponseBody(body)
        .build();
  }

  public static void getAsyncPointsFromPurchase(
      final CongratsRequest congratsRequest,
      final String purchaseId,
      final int statusCode,
      final String body) {
    MockResponse.builder()
        .withURL(buildUrlActionPurchase(congratsRequest, purchaseId).toString())
        .withMethod(HttpMethod.GET)
        .withStatusCode(statusCode)
        .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
        .withResponseHeader("Cache-Control", "max-age=0")
        .withResponseBody(body)
        .build();
  }
}
