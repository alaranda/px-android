package com.mercadolibre.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public final class CongratsTexts {
    public CongratsTexts() {
        Locale.setDefault(new Locale("DEFAULT"));
    }

    public static final String DISCOUNTS = "congrats.discounts";
    public static final String DISCOUNTS_LEVEL = "congrats.discount.level";
    public static final String DISCOUNTS_DOWNLOAD_MP = "congrats.download.app.mp";
    public static final String DISCOUNTS_DOWNLOAD_ML = "congrats.download.app.ml";
    public static final String DOWNLOAD = "congrats.download";
    public static final String SEE_ALL = "congrats.see.all.discounts";

    private ResourceBundle getResourceBundle(final Locale locale){
        return ResourceBundle.getBundle("Congrats", locale);
    }

    public String getTranslation(final Locale locale, final String key) {

        return getResourceBundle(getLocale(locale)).getString(key);
    }

    public String getTranslationDownloadForApp(final Locale locale, final String platform) {

        if (platform.equalsIgnoreCase("MP")) {
            return  getResourceBundle(getLocale(locale)).getString(DISCOUNTS_DOWNLOAD_MP);
        }
        return  getResourceBundle(getLocale(locale)).getString(DISCOUNTS_DOWNLOAD_ML);
    }

    public String createTitleDiscount(final Locale locale) {
        return getResourceBundle(getLocale(locale)).getString(DISCOUNTS);
    }

    public String createSubtitleDiscount(final Locale locale, final String quantityDiscounts) {
        final String totalDiscounts = getResourceBundle(getLocale(locale)).getString(DISCOUNTS_LEVEL);
        return quantityDiscounts.concat(" ").concat(totalDiscounts);
    }

    private Locale getLocale(Locale locale) {
        Locale localeToUse = locale;

        if (locale == null) {
            localeToUse = new Locale("DEFAULT");
        }

        return localeToUse;
    }
}
