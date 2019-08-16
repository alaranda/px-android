package com.mercadolibre.mocks;

import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;

import static com.mercadolibre.api.PreferenceTidyAPI.buildUrl;

public class MockPreferenceTidyAPI {

    public static void getPreferenceByKey(final String key, final int statusCode, final String body) {
        MockResponse.builder()
                .withURL(buildUrl(key))
                .withMethod(HttpMethod.GET)
                .withStatusCode(statusCode)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
                .withResponseHeader("Cache-Control", "max-age=0")
                .withResponseBody(body)
                .build();
    }
}
