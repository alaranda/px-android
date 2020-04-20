package com.mercadolibre.service;

import static com.mercadolibre.helper.MockTestHelper.*;
import static com.mercadolibre.service.PreferenceServiceTest.CONTEXT_ES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseCvv;
import com.mercadolibre.dto.remedy.ResponseRemedyDefault;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.service.remedy.RemedyCvv;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RemedyCvvTest {

  final RemedyCvv remedyCvv = new RemedyCvv();

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void applyRemedy_statusDetailCvvSantander_remedyCvv() {

    final RemediesRequest remediesRequest = mockRemediesRequest(123l, "123", Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("5555", "Santander", new BigDecimal(123), "back", 3);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final RemediesResponse remediesResponse =
        remedyCvv.applyRemedy(CONTEXT_ES, remediesRequest, new RemediesResponse());

    final ResponseCvv responseCvv = remediesResponse.getCvv();
    assertThat(responseCvv.getTitle(), is("El código de seguridad es inválido"));
    assertThat(responseCvv.getMessage(), is("Volvé a ingresarlo para confirmar el pago."));
    assertThat(responseCvv.getFieldSetting().getTitle(), is("Código de seguridad"));
    assertThat(
        responseCvv.getFieldSetting().getHintMessage(),
        is("Los 3 números están al dorso de tu tarjeta"));
  }

  @Test
  public void applyRemedy_statusDetailDefaultBbvv_remedyDefault() {

    final RemediesRequest remediesRequest = mockRemediesRequest(123l, "123", Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("5555", "Bbvv", new BigDecimal(123), "back", 3);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final RemediesResponse remediesResponse =
        remedyCvv.applyRemedy(CONTEXT_ES, remediesRequest, new RemediesResponse());

    final ResponseRemedyDefault responseRemedyDefault = remediesResponse.getWithOutRemedy();
    assertThat(responseRemedyDefault, nullValue());
  }

  @Test
  public void applyRemedy_statusDetailCvvIcbcSecurtyCodeFront_remedyCvvSecurityCodeFront() {

    final RemediesRequest remediesRequest = mockRemediesRequest(123l, "123", Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("8888", "Icbc", new BigDecimal(321), "front", 4);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final RemediesResponse remediesResponse =
        remedyCvv.applyRemedy(CONTEXT_ES, remediesRequest, new RemediesResponse());

    final ResponseCvv responseCvv = remediesResponse.getCvv();
    assertThat(
        responseCvv.getFieldSetting().getHintMessage(),
        is("Los 4 números están al frente de tu tarjeta"));
  }

  @Test
  public void applyRemedy_statusDetailCvvwithoutPaymentMethodRejected_remedyEmpty() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.getUserAgent()).thenReturn(UserAgent.create("PX/Android/0.0.0"));
    when(remediesRequest.getRiskExcecutionId()).thenReturn(0l);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(null);

    final RemediesResponse remediesResponse =
        remedyCvv.applyRemedy(CONTEXT_ES, remediesRequest, new RemediesResponse());

    final ResponseCvv responseCvv = remediesResponse.getCvv();
    assertThat(responseCvv, is(nullValue()));
  }
}
