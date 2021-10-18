package com.mercadolibre.utils;

import static com.mercadolibre.utils.Translations.ACCREDITATION_TIME_1_2_DAYS;
import static com.mercadolibre.utils.Translations.ACCREDITATION_TIME_INSTANTLY;
import static com.mercadolibre.utils.Translations.ACCREDITATION_TIME_N_DAYS;
import static com.mercadolibre.utils.Translations.ACCREDITATION_TIME_N_HOURS;
import static com.mercadolibre.utils.Translations.CANNOT_PAY_WITH_LINK;
import static com.mercadolibre.utils.Translations.DISCOUNTS_DOWNLOAD_MP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.junit.Before;
import org.junit.Test;

public class TranslationsTest {

  private Translations translations;

  private static final Integer[] TIME = new Integer[] {3};

  @Before
  public void setUp() {
    this.translations = Translations.INSTANCE;
  }

  @Test
  public void testGetTranslationByLocale_foundKeyAndSpanish_returnsTranslatedTextInSpanish() {
    String result =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("es-AR"), DISCOUNTS_DOWNLOAD_MP);

    assertThat(result, equalTo("Exclusivo con la app de Mercado Pago"));
  }

  @Test
  public void
      testGetTranslationByLocale_foundKeyAndInvalidLanguage_returnsTranslatedTextInSpanishArg() {

    String result =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("ack"), CANNOT_PAY_WITH_LINK);

    assertThat(result, equalTo("No podés pagar con este link de pago."));
  }

  @Test
  public void
      testGetTranslationByLocale_foundKeyAndValidLanguageButNoTranslation_returnsTranslatedTextInSpanishArg() {

    String result =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("es-ES"), CANNOT_PAY_WITH_LINK);

    assertThat(result, equalTo("No puedes pagar con este link de pago."));
  }

  @Test
  public void
      testGetTranslationByLocale_foundKeyAndInvalidLanguageButNoTranslation_returnsTranslatedTextInSpanishArg() {

    String result =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("es_AR"), CANNOT_PAY_WITH_LINK);

    assertThat(result, equalTo("No puedes pagar con este link de pago."));
  }

  @Test
  public void
      testGetTranslationByLocale_withoutArgs_returnsCheckoutPaymentMethodAccreditationTimeLabel1Translated() {

    final String ar =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("es_AR"), ACCREDITATION_TIME_INSTANTLY);

    final String br =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("pt-BR"), ACCREDITATION_TIME_INSTANTLY);

    assertEquals(ar, "El pago se acreditará al instante.");
    assertEquals(br, "O pagamento será aprovado na hora.");
  }

  @Test
  public void
      testGetTranslationByLocale_withoutArgs_returnsCheckoutPaymentMethodAccreditationTimeLabel3Translated() {

    final String ar =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("es_AR"), ACCREDITATION_TIME_1_2_DAYS);

    final String br =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("pt-BR"), ACCREDITATION_TIME_1_2_DAYS);

    assertEquals(ar, "El pago se acreditará de 1 a 2 días hábiles.");
    assertEquals(br, "O pagamento será aprovado em 1 ou 2 dias úteis.");
  }

  @Test
  public void
      testGetTranslationByLocale_withoutArgs_returnsCheckoutPaymentMethodAccreditationTimeLabel4Translated() {

    final String ar =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("es_AR"), TIME, ACCREDITATION_TIME_N_HOURS);

    final String br =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("pt-BR"), TIME, ACCREDITATION_TIME_N_HOURS);

    assertEquals(ar, "El pago se acreditará en menos de 3h.");
    assertEquals(br, "O pagamento será aprovado em menos de 3h.");
  }

  @Test
  public void
      testGetTranslationByLocale_withoutArgs_returnsCheckoutPaymentMethodAccreditationTimeLabel5Translated() {

    final String ar =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("es_AR"), TIME, ACCREDITATION_TIME_N_DAYS);

    final String br =
        this.translations.getTranslationByLocale(
            Locale.forLanguageTag("pt-BR"), TIME, ACCREDITATION_TIME_N_DAYS);

    assertEquals(ar, "El pago se acreditará en 3 días hábiles.");
    assertEquals(br, "O pagamento será aprovado em 3 dias úteis.");
  }
}
