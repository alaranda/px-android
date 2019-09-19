package com.mercadolibre.service;

import org.apache.http.client.utils.URIBuilder;

public enum  OnDemandResources {
    INSTANCE;

    public static final String createOnDemandResoucesUrl(final String key, final String density, final String locale) {
        final URIBuilder uriBuilder =  new URIBuilder()
                .setScheme("https")
                .setHost("mobile.mercadolibre.com")
                .setPath("remote_resources/image/KEY")
                .addParameter("density", density)
                .addParameter("locale", locale);

        return uriBuilder.toString().replace("KEY", key);
    }
}
