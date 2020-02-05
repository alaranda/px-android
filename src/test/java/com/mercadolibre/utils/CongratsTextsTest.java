package com.mercadolibre.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CongratsTextsTest {

    private static final Locale LOCALE_PT = new Locale("PT");
    private static final Locale INVALID_LOCALE = new Locale("invalid_language");
    private static final Locale UNKNOWN_LANGUAGE_RUSSIAN = new Locale("ru");
    private static final Locale LOCALE_ES = new Locale("es");
    CongratsTexts congratsTexts;

    @Before
    public void setUp() {
        congratsTexts = new CongratsTexts();
    }

    @Test
    public void testGetTranslation_onPTLocale_returnsPTDiscountLevel() {
        final String discountLevelText = congratsTexts.getTranslation(LOCALE_PT, CongratsTexts.SEE_ALL);
        assertThat(discountLevelText, is("Ver todos os descontos"));
    }

    @Test
    public void testGetTranslationDownloadForApp_onPtLocale_returnsPTdownloadml() {
        final String discountLevelText = congratsTexts.getTranslationDownloadForApp(LOCALE_PT, "MP");
        assertThat(discountLevelText, is("Exclusivo com o app do Mercado Libre"));
    }

    @Test
    public void testGetTranslationDownloadForApp_onPTLocale_returnsPTTitleDownloadmp() {
        final String discountLevelText = congratsTexts.getTranslationDownloadForApp(LOCALE_PT, "ML");
        assertThat(discountLevelText, is("Exclusivo com o app do Mercado Pago"));
    }

    @Test
    public void testCreateTitleDiscount_onPTLocale_returnsPTTitleDiscount() {
        final String discountLevelText = congratsTexts.createTitleDiscount(LOCALE_PT);
        assertThat(discountLevelText, is("Descontos pelo seu nível"));
    }

    @Test
    public void testCreateSubtitleDiscount_onPTLocale_returnPTSubtitleDiscount() {
        final String discountLevelText = congratsTexts.createSubtitleDiscount(LOCALE_PT, "80");
        assertThat(discountLevelText, is("80 descontos conforme seu nível"));
    }

    @Test
    public void testCreateTitleDiscount_onESLocale_returnsSpanishDiscountLevel() {
        final String discountLevelText = congratsTexts.createTitleDiscount(LOCALE_ES);
        assertThat(discountLevelText, is("Descuentos por tu nivel"));
    }

    @Test
    public void testGetTranslation_onUnknownLanguageDiscountLevel_returnsDefaultLangSpanish () {
        final String discountLevelText = congratsTexts.getTranslation(UNKNOWN_LANGUAGE_RUSSIAN, CongratsTexts.SEE_ALL);
        assertThat(discountLevelText, is("Ver todos los descuentos"));
    }

    @Test
    public void testGetTranslationDownloadForApp_onInvalidLocale_returnsDefaultLanguageSpanish() {
        final String discountLevelText = congratsTexts.getTranslationDownloadForApp(INVALID_LOCALE, "ML");
        assertThat(discountLevelText, is("Exclusivo con la app de Mercado Pago"));
    }

}
