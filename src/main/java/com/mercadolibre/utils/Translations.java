package com.mercadolibre.utils;

import com.mercadolibre.framework.i18n.I18nService;

import java.util.Locale;

public enum Translations {
    INSTANCE;

    public static final String DISCOUNTS = "congrats.discounts";
    public static final String DISCOUNTS_LEVEL = "congrats.discount.level";
    public static final String DISCOUNTS_DOWNLOAD_MP = "congrats.download.app.mp";
    public static final String DISCOUNTS_DOWNLOAD_ML = "congrats.download.app.ml";
    public static final String DOWNLOAD = "congrats.download";
    public static final String SEE_ALL = "congrats.see.all.discounts";
    public static final String PAYMENT_NOT_PROCESSED = "checkout.initpreference.error.generic";
    public static final String CANNOT_PAY_WITH_LINK= "checkout.initpreference.error.invalidpreference";
    public static final String CANNOT_PAY_JUST_FOR_COLLECT = "checkout.initpreference.error.payerequalscollector";

    private I18nService i18nService = new I18nService();

    /**
     * @param locale
     * @param key
     * @return
     */
    public String getTranslationByLocale(Locale locale, final String key) {
        // i18nService is expecting literal string
        switch (key) {
            case "congrats.discounts":
                return i18nService.tr("Descuentos por tu nivel", locale);
            case "congrats.discount.level":
                return i18nService.tr("descuentos por tu nivel", locale);
            case "congrats.download.app.mp":
                return i18nService.tr("Exclusivo con la app de Mercado Pago", locale);
            case "congrats.download.app.ml":
                return i18nService.tr("Exclusivo con la app de Mercado Libre", locale);
            case "congrats.download":
                return i18nService.tr("Descargar", locale);
            case "congrats.see.all.discounts":
                return i18nService.tr("Ver todos los descuentos", locale);
            case "checkout.initpreference.error.generic":
                return i18nService.tr("No pudimos procesar tu pago, discúlpanos.", locale);
            case "checkout.initpreference.error.invalidpreference":
                return i18nService.tr("No puedes pagar con este link de pago.", locale);
            case "checkout.initpreference.error.payerequalscollector":
                return i18nService.tr("No puedes pagar con este link, solo puedes usarlo para cobrar.", locale);
        }
        return "";
    }


}
