package com.mercadolibre.utils;

import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CongratsTextsTest {

    private static final Locale LOCALE_PT = new Locale("PT");

    @Test
    public void getTranslation_pt_discountLevel() {
        final String discountLevelText = CongratsTexts.getTranslation(LOCALE_PT, CongratsTexts.SEE_ALL);
        assertThat(discountLevelText, is("Ver todos os descontos"));
    }

    @Test
    public void getTranslationDownloadForApp_pt_downloadml() {
        final String discountLevelText = CongratsTexts.getTranslationDownloadForApp(LOCALE_PT, "ML");
        assertThat(discountLevelText, is("Exclusivo com o app do Mercado Libre"));
    }

    @Test
    public void getTranslation_pt_downloadmp() {
        final String discountLevelText = CongratsTexts.getTranslationDownloadForApp(LOCALE_PT, "MP");
        assertThat(discountLevelText, is("Exclusivo com o app do Mercado Pago"));
    }

    @Test
    public void createTitleDiscount_pt_title() {
        final String discountLevelText = CongratsTexts.createTitleDiscount(LOCALE_PT, "200");
        assertThat(discountLevelText, is("200 descontos"));
    }

    @Test
    public void createSubtitleDiscount_pt_subtitle() {
        final String discountLevelText = CongratsTexts.createSubtitleDiscount(LOCALE_PT, "80");
        assertThat(discountLevelText, is("80 descontos conforme seu nível"));
    }
}
