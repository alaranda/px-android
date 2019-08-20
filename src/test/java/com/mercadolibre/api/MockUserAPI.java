package com.mercadolibre.api;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;

import static com.mercadolibre.api.UserAPI.buildUrl;


public class MockUserAPI {

    public static void getById(final long userId, final int statusCode, final String body) {
        MockResponse.builder()
                .withURL(buildUrl(userId).toString())
                .withMethod(HttpMethod.GET)
                .withStatusCode(statusCode)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
                .withResponseHeader("Cache-Control", "max-age=0")
                .withResponseBody(body)
                .build();
    }
}
