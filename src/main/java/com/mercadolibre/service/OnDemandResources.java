package com.mercadolibre.service;

import com.mercadolibre.config.Config;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.dto.congrats.merch.Content;
import org.apache.http.client.utils.URIBuilder;
import java.util.Locale;

public enum  OnDemandResources {
    INSTANCE;

    private static final String createOnDemandResoucesUrl(final String key, final String density, final Locale locale) {
        final URIBuilder uriBuilder =  new URIBuilder()
                .setScheme(Config.getString("ondemand.url.scheme"))
                .setHost(Config.getString("ondemand.url.host"))
                .setPath("remote_resources/image/KEY")
                .addParameter("density", density)
                .addParameter("locale", locale.toString());

        return uriBuilder.toString().replace("KEY", key);
    }

    public static final String createOnDemandResoucesUrlByContent(final CongratsRequest congratsRequest, final Content content, final Locale locale) {

        if (null == content) return null;

        return createOnDemandResoucesUrl(content.getIcon(), congratsRequest.getDensity(), locale);
    }
}
