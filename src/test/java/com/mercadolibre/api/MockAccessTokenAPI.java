package com.mercadolibre.api;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;


public class MockAccessTokenAPI {

    public static void getAccessToken(String accessTokenID, int statusCode, String body) {
        MockResponse.builder()
                .withURL(AccessTokenAPI.buildUrl(accessTokenID).toString())
                .withMethod(HttpMethod.GET)
                .withStatusCode(statusCode)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
                .withResponseHeader("Cache-Control", "max-age=0")
                .withResponseBody(body)
                .build();
    }
}
