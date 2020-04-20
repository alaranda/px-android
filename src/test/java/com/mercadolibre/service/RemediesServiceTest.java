package com.mercadolibre.service;

import static com.mercadolibre.helper.MockTestHelper.CONTEXT_ES;
import static com.mercadolibre.helper.MockTestHelper.mockPayerPaymentMethod;
import static com.mercadolibre.helper.MockTestHelper.mockRemediesRequest;
import static com.mercadolibre.px.toolkit.constants.PaymentTypeId.CREDIT_CARD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockPaymentAPI;
import com.mercadolibre.api.MockRiskApi;
import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.Installment;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseCvv;
import com.mercadolibre.dto.remedy.ResponseHighRisk;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import spark.utils.IOUtils;

public class RemediesServiceTest {

  private static final String PAYMENT_ID_TEST = "123456789";
  private static final String CALLER_ID_TEST = "11111";
  private RemediesService remediesService = new RemediesService();

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void getRemedy_statusDetailCvvSantander_remedyCvv() throws IOException, ApiException {

    MockPaymentAPI.getPayment(
        PAYMENT_ID_TEST,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/rejected_cvv.json")));

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123l, CALLER_ID_TEST, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("5555", "Santander", new BigDecimal(123), "back", 3);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final RemediesResponse remediesResponse =
        remediesService.getRemedy(CONTEXT_ES, PAYMENT_ID_TEST, remediesRequest);

    final ResponseCvv responseCvv = remediesResponse.getCvv();
    assertThat(responseCvv.getTitle(), notNullValue());
    assertThat(responseCvv.getMessage(), notNullValue());
    assertThat(responseCvv.getFieldSetting(), notNullValue());
    assertThat(responseCvv.getFieldSetting().getHintMessage(), notNullValue());
  }

  @Test
  public void getRemedy_statusDetailHighRiskKycPatagonia_remedyHighRiskML()
      throws IOException, ApiException {

    MockPaymentAPI.getPayment(
        PAYMENT_ID_TEST,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/rejected_highRisk.json")));

    MockRiskApi.getRisk(
        123L,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/risk/17498128727.json")));

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123l, CALLER_ID_TEST, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("2222", "Patagonia", new BigDecimal(123), "back", 3);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final Context context =
        Context.builder().requestId("").locale("es-AR").platform(Platform.ML).build();
    final RemediesResponse remediesResponse =
        remediesService.getRemedy(context, "123456789", remediesRequest);

    final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
    assertThat(
        responseHighRisk.getDeepLink(),
        is("meli://kyc/?initiative=px-high-risk&callback=meli://px/one_tap"));
  }

  @Test
  public void applyRemedy_statusDetailMaxAttempsWithoutEsc_remedySuggestedPmAndCvv()
      throws ApiException, IOException {

    MockPaymentAPI.getPayment(
        PAYMENT_ID_TEST,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/rejected_other_reason.json")));

    final BigDecimal totalAmount = new BigDecimal(200);

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123l, CALLER_ID_TEST, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("1234", "Santander", totalAmount, "back", 3);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getPayerPaymentMethodRejected().getInstallments()).thenReturn(3);

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        Mockito.mock(AlternativePayerPaymentMethod.class);
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(CREDIT_CARD.name());
    when(alternativePayerPaymentMethod.getPaymentMethodId()).thenReturn("visa");
    when(alternativePayerPaymentMethod.getEscStatus()).thenReturn("rejected");
    when(alternativePayerPaymentMethod.getIssuerName()).thenReturn("Patagonia");

    final Installment installment = Mockito.mock(Installment.class);
    when(installment.getInstallments()).thenReturn(3);
    when(installment.getTotalAmount()).thenReturn(totalAmount);
    when(alternativePayerPaymentMethod.getInstallmentsList())
        .thenReturn(Arrays.asList(installment));

    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(Arrays.asList(alternativePayerPaymentMethod));

    final RemediesResponse remediesResponse =
        remediesService.getRemedy(CONTEXT_ES, "123456789", remediesRequest);

    assertThat(remediesResponse.getSuggestedPaymentMethod().getTitle(), notNullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getMessage(), notNullValue());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(CREDIT_CARD.name()));
    assertThat(remediesResponse.getCvv().getTitle(), notNullValue());
    assertThat(remediesResponse.getCvv().getMessage(), notNullValue());
  }
}
