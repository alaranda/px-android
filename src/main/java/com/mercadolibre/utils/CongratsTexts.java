package com.mercadolibre.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public final class CongratsTexts {
    private CongratsTexts() {
        throw new IllegalAccessError();
    }

    public static final String DISCOUNTS = "congrats.discounts";
    public static final String DISCOUNTS_LEVEL = "congrats.discount.level";
    public static final String DISCOUNTS_DOWNLOAD_MP = "congrats.download.app.mp";
    public static final String DISCOUNTS_DOWNLOAD_ML = "congrats.download.app.ml";
    public static final String DOWNLOAD = "congrats.download";
    public static final String SEE_ALL = "congrats.see.all.discounts";

    private static ResourceBundle getResourceBundle(final Locale locale){
        return ResourceBundle.getBundle("Congrats", locale);
    }

    public static String getTranslation(final Locale locale, final String key) {

        return getResourceBundle(locale).getString(key);
    }

    public static String getTranslationDownloadForApp(final Locale locale, final String platform) {

        if (platform.equalsIgnoreCase("MP")) {
            return  getResourceBundle(locale).getString(DISCOUNTS_DOWNLOAD_MP);
        }
        return  getResourceBundle(locale).getString(DISCOUNTS_DOWNLOAD_ML);
    }

    public static String createTitleDiscount(final Locale locale) {
        return getResourceBundle(locale).getString(DISCOUNTS);
    }

    public static String createSubtitleDiscount(final Locale locale, final String quantityDiscounts) {
        final String totalDiscounts = getResourceBundle(locale).getString(DISCOUNTS_LEVEL);
        return quantityDiscounts.concat(" ").concat(totalDiscounts);
    }
}
