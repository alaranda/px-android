package com.mercadolibre.api;

import static com.mercadolibre.px.constants.HeadersConstants.REQUEST_ID;

import com.mercadolibre.px.api.lib.core.SiteDao;
import com.mercadolibre.px_config.Config;
import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;
import org.apache.http.client.utils.URIBuilder;

public class MockSiteAPI {

  public static void getSiteAsync(final String id, final int statusCode, final String body) {
    final URIBuilder uriBuilder =
        new URIBuilder()
            .setScheme(Config.getString("api.base.url.scheme"))
            .setHost(Config.getString("api.base.url.host"))
            .setPath(String.format(SiteDao.GET_SITE_PATH, id));

    MockResponse.builder()
        .withURL(uriBuilder.toString())
        .withMethod(HttpMethod.GET)
        .withStatusCode(statusCode)
        .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
        .withResponseHeader("Cache-Control", "max-age=0")
        .withResponseBody(body)
        .withRequestHeader(REQUEST_ID, "test-request-id")
        .build();
  }
}
