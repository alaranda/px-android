package com.mercadolibre.api;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;

import static com.mercadolibre.api.FraudApi.getPath;

public class MockFraudApi {

    public static void resetCapEsc(final String cardId, final String clientId, final int statusCode) {
        MockResponse.builder()
                .withURL(getPath(cardId).toString())
                .withMethod(HttpMethod.DELETE)
                .withStatusCode(statusCode)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
                .withResponseHeader("Cache-Control", "max-age=0")
                .withResponseBody("")
                .build();
    }
}
