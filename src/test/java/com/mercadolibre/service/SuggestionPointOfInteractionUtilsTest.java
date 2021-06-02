package com.mercadolibre.service;

import static com.mercadolibre.constants.Constants.STATUS_APPROVED;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.CREDIT_CARD;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.DEBIT_CARD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.PaymentMethodSelected;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.utils.SuggestionPaymentMehodsUtils;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SuggestionPointOfInteractionUtilsTest {

  final SuggestionPaymentMehodsUtils suggestionPaymentMehodsUtils =
      new SuggestionPaymentMehodsUtils();

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void getPaymentMethodSelected_emptyAlternativePaymentMethods_remedyEmpty() {

    final PaymentMethodSelected paymentMethodSelected =
        suggestionPaymentMehodsUtils.getPaymentMethodSelected(new ArrayList<>());

    assertThat(paymentMethodSelected, nullValue());
  }

  @Test
  public void getPaymentMethodSelected_tcEscRequired_remedyEmpty() {

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        Mockito.mock(AlternativePayerPaymentMethod.class);
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(CREDIT_CARD);
    when(alternativePayerPaymentMethod.getEscStatus()).thenReturn("rejected");
    final PaymentMethodSelected paymentMethodSelected =
        suggestionPaymentMehodsUtils.getPaymentMethodSelected(
            Arrays.asList(alternativePayerPaymentMethod));

    assertThat(paymentMethodSelected.isRemedyCvvRequired(), is(true));
  }

  @Test
  public void getPaymentMethodSelected_tdEscRequired_remedyEmpty() {

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        Mockito.mock(AlternativePayerPaymentMethod.class);
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(DEBIT_CARD);
    when(alternativePayerPaymentMethod.getEscStatus()).thenReturn("rejected");
    final PaymentMethodSelected paymentMethodSelected =
        suggestionPaymentMehodsUtils.getPaymentMethodSelected(
            Arrays.asList(alternativePayerPaymentMethod));

    assertThat(paymentMethodSelected.isRemedyCvvRequired(), is(true));
  }

  @Test
  public void getPaymentMethodSelected_tdEscNotRequired_remedyEmpty() {

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        Mockito.mock(AlternativePayerPaymentMethod.class);
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(DEBIT_CARD);
    when(alternativePayerPaymentMethod.getEscStatus()).thenReturn(STATUS_APPROVED);
    final PaymentMethodSelected paymentMethodSelected =
        suggestionPaymentMehodsUtils.getPaymentMethodSelected(
            Arrays.asList(alternativePayerPaymentMethod));

    assertThat(paymentMethodSelected.isRemedyCvvRequired(), is(false));
  }

  @Test
  public void getPaymentMethodSelected_tcEscNotRequired_remedyEmpty() {

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        Mockito.mock(AlternativePayerPaymentMethod.class);
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(CREDIT_CARD);
    when(alternativePayerPaymentMethod.getEscStatus()).thenReturn(STATUS_APPROVED);
    final PaymentMethodSelected paymentMethodSelected =
        suggestionPaymentMehodsUtils.getPaymentMethodSelected(
            Arrays.asList(alternativePayerPaymentMethod));

    assertThat(paymentMethodSelected.isRemedyCvvRequired(), is(false));
  }
}
