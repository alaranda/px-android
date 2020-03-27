package com.mercadolibre.utils;

import com.mercadolibre.framework.i18n.I18nService;

import java.util.Locale;

import static org.apache.commons.lang3.LocaleUtils.isAvailableLocale;

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
    public static final String REMEDY_CVV_TITLE = "remedy.cvv.title";
    public static final String REMEDY_CVV_MESSAGE = "remedy.cvv.message";
    public static final String REMEDY_FIELD_SETTING_CVV_TITLE = "remedy.fieldsetting.cvv.title";
    public static final String REMEDY_FIELD_SETTING_CVV_HINT_MESSAGE_BACK = "remedy.fieldsetting.cvv.hintmessage.back";
    public static final String REMEDY_FIELD_SETTING_CVV_HINT_MESSAGE_FRONT = "remedy.fieldsetting.cvv.hintmessage.front";
    public static final String REMEDY_HIGH_RISK_TITLE = "remedy.highrisk.title";
    public static final String REMEDY_HIGH_RISK_MESSAGE = "remedy.highrisk.message";
    public static final String REMEDY_HIGH_RISK_BUTTON_LOUD = "remedy.highrisk.button.loud";
    public static final String VIEW_RECEIPT = "view.receipt";
    public static final String IFPE_COMPLIANCE_MESSAGE = "ifpe.compliance_message";

    private I18nService i18nService = new I18nService();

    /**
     * @param locale
     * @param key
     * @return
     */
    public String getTranslationByLocale(Locale locale, final String key) {
        if (!isAvailableLocale(locale)) {
            locale = Locale.forLanguageTag("es-AR");
        }
        // i18nService is expecting literal string
        switch (key) {
            case DISCOUNTS:
                return i18nService.tr("Descuentos por tu nivel", locale);
            case DISCOUNTS_LEVEL:
                return i18nService.tr("descuentos por tu nivel", locale);
            case DISCOUNTS_DOWNLOAD_MP:
                return i18nService.tr("Exclusivo con la app de Mercado Pago", locale);
            case DISCOUNTS_DOWNLOAD_ML:
                return i18nService.tr("Exclusivo con la app de Mercado Libre", locale);
            case DOWNLOAD:
                return i18nService.tr("Descargar", locale);
            case SEE_ALL:
                return i18nService.tr("Ver todos los descuentos", locale);
            case PAYMENT_NOT_PROCESSED:
                return i18nService.tr("No pudimos procesar tu pago, discúlpanos.", locale);
            case CANNOT_PAY_WITH_LINK:
                return i18nService.tr("No puedes pagar con este link de pago.", locale);
            case CANNOT_PAY_JUST_FOR_COLLECT:
                return i18nService.tr("No puedes pagar con este link, solo puedes usarlo para cobrar.", locale);
            case REMEDY_CVV_TITLE:
                return i18nService.tr("El código de seguridad es inválido", locale);
            case REMEDY_CVV_MESSAGE:
                return i18nService.tr("Vuelve a ingresarlo para confirmar el pago.", locale);
            case REMEDY_FIELD_SETTING_CVV_TITLE:
                return i18nService.tr("Código de seguridad", locale);
            case REMEDY_FIELD_SETTING_CVV_HINT_MESSAGE_BACK:
                return i18nService.tr("Los 3 números están al dorso de tu tarjeta", locale);
            case REMEDY_FIELD_SETTING_CVV_HINT_MESSAGE_FRONT:
                return i18nService.tr("Los 4 números están al frente de tu tarjeta", locale);
            case REMEDY_HIGH_RISK_TITLE:
                return i18nService.tr("Valida tu identidad para realizar el pago", locale);
            case REMEDY_HIGH_RISK_MESSAGE:
                return i18nService.tr("Te pediremos algunos datos. Ten a mano tu DNI. Solo te llevará unos minutos.", locale);
            case REMEDY_HIGH_RISK_BUTTON_LOUD:
                return i18nService.tr("Validar identidad", locale);
            case VIEW_RECEIPT:
                return i18nService.tr("Ver comprobante de pago", locale);
            case IFPE_COMPLIANCE_MESSAGE:
                return i18nService.tr("A partir de ahora, tu cuenta estará bajo la modalidad Mercado Libre IFPE. Usaremos el método de seguridad de tu teléfono para ingresar y pagar con la aplicación.", locale);
            default:
                return "";
        }
    }


}
