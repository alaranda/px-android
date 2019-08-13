package com.mercadolibre.utils;

import com.mercadolibre.constants.HeadersConstants;
import spark.Request;

import static org.apache.commons.lang.StringUtils.isBlank;

public class Locale {

    public static java.util.Locale getLocale(final String language) {
        return isBlank(language) ? java.util.Locale.ROOT : java.util.Locale.forLanguageTag(language);
    }

    public static java.util.Locale getLocale(final Request request) {
        final String language = request.headers(HeadersConstants.LANGUAGE);
        return isBlank(language) ? java.util.Locale.ROOT : java.util.Locale.forLanguageTag(language);
    }
}
