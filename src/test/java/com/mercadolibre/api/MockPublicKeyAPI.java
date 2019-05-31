package com.mercadolibre.api;

import com.mercadolibre.constants.HeadersConstants;
import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;

public class MockPublicKeyAPI {

    public static void getPublicKey(String publicKeyID, int statusCode, String body) {
        MockResponse.builder()
                .withURL(PublicKeyAPI.getPath(publicKeyID).toString())
                .withMethod(HttpMethod.GET)
                .withStatusCode(statusCode)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
                .withResponseHeader(HeadersConstants.NO_CACHE_PARAMS, "max-age=0")
                .withResponseBody(body)
                .build();
    }

    public static void getBycallerIdAndClientId(final String callerId, final Long clientId, int statusCode, String body) {
        MockResponse.builder()
                .withURL(PublicKeyAPI.getPathWithParams(callerId, clientId).toString())
                .withMethod(HttpMethod.GET)
                .withStatusCode(statusCode)
                .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
                .withResponseHeader(HeadersConstants.NO_CACHE_PARAMS, "max-age=0")
                .withResponseBody(body)
                .build();
    }

}
