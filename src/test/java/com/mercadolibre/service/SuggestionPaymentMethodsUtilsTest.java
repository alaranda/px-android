package com.mercadolibre.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.PaymentMethodSelected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.utils.SuggestionPaymentMehodsUtils;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SuggestionPaymentMethodsUtilsTest {

  final SuggestionPaymentMehodsUtils suggestionPaymentMehodsUtils =
      new SuggestionPaymentMehodsUtils();

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void findPaymentMethodSuggestionsAmount_emptyAlternativePaymentMethods_remedyEmpty() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.getAlternativePayerPaymentMethods()).thenReturn(new ArrayList<>());

    final PaymentMethodSelected paymentMethodSelected =
        suggestionPaymentMehodsUtils.findPaymentMethodSuggestionsAmount(remediesRequest);

    assertThat(paymentMethodSelected, nullValue());
  }

  @Test
  public void findPaymentMethodSuggestionsAmount_nullAlternativePaymentMethods_remedyEmpty() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.getAlternativePayerPaymentMethods()).thenReturn(null);

    final PaymentMethodSelected paymentMethodSelected =
        suggestionPaymentMehodsUtils.findPaymentMethodSuggestionsAmount(remediesRequest);

    assertThat(paymentMethodSelected, nullValue());
  }

  @Test
  public void findPaymentMethodSuggestionsAmount_nullpayerPaymentMethodRejected_remedyEmpty() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(null);
    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        AlternativePayerPaymentMethod.builder().build();
    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(Arrays.asList(alternativePayerPaymentMethod));
    final PaymentMethodSelected paymentMethodSelected =
        suggestionPaymentMehodsUtils.findPaymentMethodSuggestionsAmount(remediesRequest);

    assertThat(paymentMethodSelected, nullValue());
  }
}
