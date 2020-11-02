package com.mercadolibre.service;

import static com.mercadolibre.constants.Constants.STATUS_APPROVED;
import static com.mercadolibre.helper.MockTestHelper.*;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.ACCOUNT_MONEY;
import static com.mercadolibre.px.toolkit.constants.PaymentTypeId.CREDIT_CARD;
import static com.mercadolibre.px.toolkit.constants.PaymentTypeId.DEBIT_CARD;
import static com.mercadolibre.utils.Translations.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.Installment;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.context.UserAgent;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.service.remedy.RemedyCvv;
import com.mercadolibre.service.remedy.RemedySuggestionPaymentMethod;
import com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RemedySuggestionPaymentMethodTest {

  private static final String CALLER_ID_TEST = "123456789";
  final RemedyCvv remedyCvv = new RemedyCvv();

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void applyRemedy_statusDetailOtherReason_remedySuggestedPmEsc() {

    final BigDecimal totalAmount = new BigDecimal(100);

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123l, CALLER_ID_TEST, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("1234", "Santander", totalAmount, "back", 3);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn("credit_card");
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getPayerPaymentMethodRejected().getInstallments()).thenReturn(3);

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        Mockito.mock(AlternativePayerPaymentMethod.class);
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(CREDIT_CARD.name());
    when(alternativePayerPaymentMethod.getEscStatus()).thenReturn("approved");
    when(alternativePayerPaymentMethod.getIssuerName()).thenReturn("BBVA");
    when(alternativePayerPaymentMethod.isEsc()).thenReturn(true);

    final Installment installment = Mockito.mock(Installment.class);
    when(installment.getInstallments()).thenReturn(3);
    when(installment.getTotalAmount()).thenReturn(totalAmount);
    when(alternativePayerPaymentMethod.getInstallmentsList())
        .thenReturn(Arrays.asList(installment));

    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(Arrays.asList(alternativePayerPaymentMethod));

    final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            remedyCvv, REMEDY_OTHER_REASON_TITLE, REMEDY_OTHER_REASON_MESSAGE);

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());

    assertThat(remediesResponse.getSuggestedPaymentMethod().getTitle(), notNullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getMessage(), notNullValue());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(CREDIT_CARD.name()));
    assertThat(remediesResponse.getCvv(), nullValue());
  }

  @Test
  public void applyRemedy_statusDetailMaxAttempsWithoutEsc_remedySuggestedPmAndCvv() {

    final BigDecimal totalAmount = new BigDecimal(200);
    final RemediesRequest remediesRequest =
        mockRemediesRequest(123l, CALLER_ID_TEST, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("1234", "Santander", totalAmount, "back", 3);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn("credit_card");
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

    final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            remedyCvv, REMEDY_MAX_ATTEMPTS_TITLE, REMEDY_MAX_ATTEMPTS_MESSAGE);

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());

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

  @Test
  public void applyRemedy_statusDetailBlacklist_remedySuggestedPmAccountMoney() {

    final BigDecimal totalAmount = new BigDecimal(100);

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123l, CALLER_ID_TEST, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("1234", "Santander", totalAmount, "back", 3);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn("credit_card");
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
    when(remediesRequest.isOneTap()).thenReturn(true);

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        Mockito.mock(AlternativePayerPaymentMethod.class);
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(ACCOUNT_MONEY);
    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(Arrays.asList(alternativePayerPaymentMethod));

    final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            remedyCvv, REMEDY_BLACKLIST_TITLE, REMEDY_BLACKLIST_MESSAGE);

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());

    assertThat(remediesResponse.getSuggestedPaymentMethod().getTitle(), notNullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getMessage(), notNullValue());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(ACCOUNT_MONEY));
    assertThat(remediesResponse.getCvv(), nullValue());
  }

  @Test
  public void applyRemedy_statusDetailInvalidInstallments_remedySuggestedPmDebitCard() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.isOneTap()).thenReturn(true);
    PayerPaymentMethodRejected payerPaymentMethodRejected =
        Mockito.mock(PayerPaymentMethodRejected.class);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn("credit_card");
    when(payerPaymentMethodRejected.getTotalAmount()).thenReturn(new BigDecimal(123));
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        Mockito.mock(AlternativePayerPaymentMethod.class);
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(DEBIT_CARD.name());
    when(alternativePayerPaymentMethod.getEscStatus()).thenReturn(STATUS_APPROVED);
    when(alternativePayerPaymentMethod.isEsc()).thenReturn(true);
    final Installment installment = Mockito.mock(Installment.class);
    when(installment.getInstallments()).thenReturn(5);
    when(installment.getTotalAmount()).thenReturn(new BigDecimal(333));
    when(alternativePayerPaymentMethod.getInstallmentsList())
        .thenReturn(Arrays.asList(installment));
    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(Arrays.asList(alternativePayerPaymentMethod));

    final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            remedyCvv, REMEDY_INVALID_INSTALLMENTS_TITLE, REMEDY_INVALID_INSTALLMENTS_MESSAGE);

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());

    assertThat(remediesResponse.getSuggestedPaymentMethod().getTitle(), notNullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getMessage(), notNullValue());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(DEBIT_CARD.name()));
    assertThat(remediesResponse.getCvv(), nullValue());
  }

  @Test
  public void applyRemedy_statusBadFilledOtherOneTapFalse_remedyEmpty() {

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123l, CALLER_ID_TEST, Site.MLA.name());
    when(remediesRequest.isOneTap()).thenReturn(false);

    final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            remedyCvv, REMEDY_BAD_FILLED_OTHER_TITLE, REMEDY_BAD_FILLED_OTHER_MESSAGE);

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());

    assertThat(remediesResponse.getSuggestedPaymentMethod(), nullValue());
    assertThat(remediesResponse.getCvv(), nullValue());
  }

  @Test
  public void applyRemedy_rejectedTC_returnCC() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.isOneTap()).thenReturn(true);
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethodRejected(PaymentMethodsRejectedTypes.CREDIT_CARD);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final List<AlternativePayerPaymentMethod> alternativePayerPaymentMethodList =
        mockAlternativePaymentMethodsList();
    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(alternativePayerPaymentMethodList);

    final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            remedyCvv, REMEDY_OTHER_REASON_TITLE, REMEDY_OTHER_REASON_MESSAGE);

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());

    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(PaymentMethodsRejectedTypes.CONSUMER_CREDITS));
  }

  @Test
  public void applyRemedy_rejectedAM_returnCC() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.isOneTap()).thenReturn(true);
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethodRejected(PaymentMethodsRejectedTypes.ACCOUNT_MONEY);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final List<AlternativePayerPaymentMethod> alternativePayerPaymentMethodList =
        mockAlternativePaymentMethodsList();
    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(alternativePayerPaymentMethodList);

    final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            remedyCvv, REMEDY_OTHER_REASON_TITLE, REMEDY_OTHER_REASON_MESSAGE);

    final Context context = mockContextLibDto();
    when(context.getUserAgent()).thenReturn(UserAgent.create("PX/iOS/4.36.4"));

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(context, remediesRequest, new RemediesResponse());

    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(PaymentMethodsRejectedTypes.CONSUMER_CREDITS));
  }

  @Test
  public void applyRemedy_rejectedTD_returnAM() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.isOneTap()).thenReturn(true);
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethodRejected(PaymentMethodsRejectedTypes.DEBIT_CARD);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final List<AlternativePayerPaymentMethod> alternativePayerPaymentMethodList =
        mockAlternativePaymentMethodsList();
    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(alternativePayerPaymentMethodList);

    final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            remedyCvv, REMEDY_OTHER_REASON_TITLE, REMEDY_OTHER_REASON_MESSAGE);

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());

    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(PaymentMethodsRejectedTypes.ACCOUNT_MONEY));
  }

  @Test
  public void applyRemedy_rejectedAMExcludeCredits_returnCreditCard() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.isOneTap()).thenReturn(true);
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethodRejected(PaymentMethodsRejectedTypes.ACCOUNT_MONEY);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final List<AlternativePayerPaymentMethod> alternativePayerPaymentMethodList =
        mockAlternativePaymentMethodsList();
    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(alternativePayerPaymentMethodList);

    final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            remedyCvv, REMEDY_OTHER_REASON_TITLE, REMEDY_OTHER_REASON_MESSAGE);

    final Context context = mockContextLibDto();
    when(context.getUserAgent()).thenReturn(UserAgent.create("PX/iOS/4.36.0"));

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(context, remediesRequest, new RemediesResponse());

    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(PaymentMethodsRejectedTypes.CREDIT_CARD));
  }
}
