package com.mercadolibre.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Locale;
import org.junit.Before;
import org.junit.Test;

public class TranslationsTest {

  private Translations translations;

  @Before
  public void setUp() {
    this.translations = Translations.INSTANCE;
  }

  @Test
  public void testGetTranslationByLocale_foundKeyAndSpanish_returnsTranslatedTextInSpanish() {
    String result =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("es-AR"), Translations.DISCOUNTS_DOWNLOAD_MP);

    assertThat(result, equalTo("Exclusivo con la app de Mercado Pago"));
  }

  @Test
  public void
      testGetTranslationByLocale_foundKeyAndInvalidLanguage_returnsTranslatedTextInSpanishArg() {

    String result =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("ack"), Translations.CANNOT_PAY_WITH_LINK);

    assertThat(result, equalTo("No podés pagar con este link de pago."));
  }

  @Test
  public void
      testGetTranslationByLocale_foundKeyAndValidLanguageButNoTranslation_returnsTranslatedTextInSpanishArg() {

    String result =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("es-ES"), Translations.CANNOT_PAY_WITH_LINK);

    assertThat(result, equalTo("No puedes pagar con este link de pago."));
  }

  @Test
  public void
      testGetTranslationByLocale_foundKeyAndInvalidLanguageButNoTranslation_returnsTranslatedTextInSpanishArg() {

    String result =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("es_AR"), Translations.CANNOT_PAY_WITH_LINK);

    assertThat(result, equalTo("No puedes pagar con este link de pago."));
  }
}
