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

import com.mercadolibre.dto.remedy.*;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.context.UserAgent;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.dto.lib.text.Text;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.service.remedy.RemedyCvv;
import com.mercadolibre.service.remedy.RemedySuggestionPaymentMethod;
import com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes;
import java.math.BigDecimal;
import java.util.*;
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
        mockRemediesRequest(123L, CALLER_ID_TEST, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("1234", "Santander", totalAmount, "back", 3);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn("credit_card");
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getPayerPaymentMethodRejected().getInstallments()).thenReturn(3);
    when(remediesRequest.getPayerPaymentMethodRejected().getBin()).thenReturn("12344321");

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        Mockito.mock(AlternativePayerPaymentMethod.class);
    when(alternativePayerPaymentMethod.getCustomOptionId()).thenReturn("1234567890");
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(CREDIT_CARD.name());
    when(alternativePayerPaymentMethod.getEscStatus()).thenReturn("approved");
    when(alternativePayerPaymentMethod.getIssuerName()).thenReturn("BBVA");
    when(alternativePayerPaymentMethod.isEsc()).thenReturn(true);
    when(alternativePayerPaymentMethod.getBin()).thenReturn("1234554321");

    final Installment installment = Mockito.mock(Installment.class);
    when(installment.getInstallments()).thenReturn(3);
    when(installment.getTotalAmount()).thenReturn(totalAmount);
    when(alternativePayerPaymentMethod.getInstallmentsList())
        .thenReturn(Collections.singletonList(installment));

    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(Collections.singletonList(alternativePayerPaymentMethod));

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
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar con crédito"));
  }

  @Test
  public void applyRemedy_statusDetailMaxAttemptsWithoutEsc_remedySuggestedPmAndCvv() {

    final BigDecimal totalAmount = new BigDecimal(200);
    final RemediesRequest remediesRequest =
        mockRemediesRequest(123L, CALLER_ID_TEST, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("1234", "Santander", totalAmount, "back", 3);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn("credit_card");
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getPayerPaymentMethodRejected().getInstallments()).thenReturn(3);

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        Mockito.mock(AlternativePayerPaymentMethod.class);
    when(alternativePayerPaymentMethod.getCustomOptionId()).thenReturn("1234567890");
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(CREDIT_CARD.name());
    when(alternativePayerPaymentMethod.getPaymentMethodId()).thenReturn("visa");
    when(alternativePayerPaymentMethod.getEscStatus()).thenReturn("rejected");
    when(alternativePayerPaymentMethod.getIssuerName()).thenReturn("Patagonia");
    when(alternativePayerPaymentMethod.getBin()).thenReturn("1234554321");

    final Installment installment = Mockito.mock(Installment.class);
    when(installment.getInstallments()).thenReturn(3);
    when(installment.getTotalAmount()).thenReturn(totalAmount);
    when(alternativePayerPaymentMethod.getInstallmentsList())
        .thenReturn(Collections.singletonList(installment));

    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(Collections.singletonList(alternativePayerPaymentMethod));

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
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar con crédito"));
  }

  @Test
  public void applyRemedy_statusDetailBlacklist_remedySuggestedPmAccountMoney() {

    final BigDecimal totalAmount = new BigDecimal(100);

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123L, CALLER_ID_TEST, Site.MLA.name());
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("1234", "Santander", totalAmount, "back", 3);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn("credit_card");
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
    when(remediesRequest.isOneTap()).thenReturn(true);

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        Mockito.mock(AlternativePayerPaymentMethod.class);
    when(alternativePayerPaymentMethod.getCustomOptionId()).thenReturn("1234567890");
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(ACCOUNT_MONEY);
    when(alternativePayerPaymentMethod.getBin()).thenReturn("1234554321");
    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(Collections.singletonList(alternativePayerPaymentMethod));

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
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar con dinero disponible en Mercado Pago"));
  }

  @Test
  public void applyRemedy_statusDetailInvalidInstallments_remedySuggestedPmDebitCard() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.isOneTap()).thenReturn(true);
    PayerPaymentMethodRejected payerPaymentMethodRejected =
        Mockito.mock(PayerPaymentMethodRejected.class);
    when(payerPaymentMethodRejected.getCustomOptionId()).thenReturn("12345678901");
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn("credit_card");
    when(payerPaymentMethodRejected.getTotalAmount()).thenReturn(new BigDecimal(123));
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
        Mockito.mock(AlternativePayerPaymentMethod.class);
    when(alternativePayerPaymentMethod.getCustomOptionId()).thenReturn("1234567890");
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(DEBIT_CARD.name());
    when(alternativePayerPaymentMethod.getEscStatus()).thenReturn(STATUS_APPROVED);
    when(alternativePayerPaymentMethod.isEsc()).thenReturn(true);
    when(alternativePayerPaymentMethod.getBin()).thenReturn("1234554321");

    final Installment installment = Mockito.mock(Installment.class);
    when(installment.getInstallments()).thenReturn(5);
    when(installment.getTotalAmount()).thenReturn(new BigDecimal(333));
    when(alternativePayerPaymentMethod.getInstallmentsList())
        .thenReturn(Collections.singletonList(installment));
    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(Collections.singletonList(alternativePayerPaymentMethod));

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
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar"));
  }

  @Test
  public void applyRemedy_statusBadFilledOtherOneTapFalse_remedyEmpty() {

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123L, CALLER_ID_TEST, Site.MLA.name());
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
  public void applyRemedy_rejectedTC_returnTC() {

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
        is(PaymentMethodsRejectedTypes.CREDIT_CARD));
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar con crédito"));
  }

  @Test
  public void applyRemedy_rejectedAM_returnTC() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getSiteId()).thenReturn("MLB");
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
    when(context.getLocale()).thenReturn(Locale.forLanguageTag("pt-BR"));

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(context, remediesRequest, new RemediesResponse());

    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(PaymentMethodsRejectedTypes.CREDIT_CARD));
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar com crédito"));
  }

  @Test
  public void applyRemedy_rejectedTD_returnAM() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getSiteId()).thenReturn("MLB");
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

    final Context context = mockContextLibDto();
    when(context.getLocale()).thenReturn(Locale.forLanguageTag("pt-BR"));

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(context, remediesRequest, new RemediesResponse());

    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(PaymentMethodsRejectedTypes.ACCOUNT_MONEY));
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar"));
  }

  @Test
  public void applyRemedy_rejectedAMExcludeCredits_returnCreditCard() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getSiteId()).thenReturn("MLA");
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
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar con crédito"));
  }

  @Test
  public void applyRemedy_rejectedAMExcludeCreditsForSite_returnCreditCard() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getSiteId()).thenReturn("MLM");
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
    when(context.getUserAgent()).thenReturn(UserAgent.create("PX/iOS/4.37.5"));

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(context, remediesRequest, new RemediesResponse());

    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(PaymentMethodsRejectedTypes.CREDIT_CARD));
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar con crédito"));
  }

  @Test
  public void applyRemedy_verify_custom_message_label() {

    CustomStringConfiguration customStringConfiguration = new CustomStringConfiguration();
    customStringConfiguration.setTotalDescriptionText("Some text       ");

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getSiteId()).thenReturn("MLB");
    when(remediesRequest.getCustomStringConfiguration()).thenReturn(customStringConfiguration);
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
    when(context.getUserAgent()).thenReturn(UserAgent.create("PX/iOS/4.37.5"));
    when(context.getLocale()).thenReturn(Locale.forLanguageTag("pt-BR"));

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(context, remediesRequest, new RemediesResponse());

    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(PaymentMethodsRejectedTypes.CREDIT_CARD));
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Some text com crédito"));
  }

  @Test
  public void applyRemedy_verify_custom_message_label_and_hybrid_case_account_money() {

    CustomStringConfiguration customStringConfiguration = new CustomStringConfiguration();
    customStringConfiguration.setTotalDescriptionText("Some text       ");

    final BigDecimal totalAmount = new BigDecimal(100);

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123L, CALLER_ID_TEST, Site.MLA.name());
    when(remediesRequest.getCustomStringConfiguration()).thenReturn(customStringConfiguration);
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("1234", "Santander", totalAmount, "back", 3);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn("credit_card");
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getPayerPaymentMethodRejected().getInstallments()).thenReturn(3);
    when(remediesRequest.getPayerPaymentMethodRejected().getBin()).thenReturn("12344321");

    AlternativePayerPaymentMethod.AlternativePayerPaymentMethodBuilder
        alternativePayerPaymentMethodBuilder = AlternativePayerPaymentMethod.builder();
    alternativePayerPaymentMethodBuilder
        .customOptionId("1234567890")
        .paymentTypeId(ACCOUNT_MONEY)
        .escStatus("approved")
        .issuerName("BBVA")
        .esc(true)
        .bin("407843");

    final Installment installment = Mockito.mock(Installment.class);
    when(installment.getInstallments()).thenReturn(3);
    when(installment.getTotalAmount()).thenReturn(totalAmount);

    alternativePayerPaymentMethodBuilder.installmentsList(Collections.singletonList(installment));

    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(Collections.singletonList(alternativePayerPaymentMethodBuilder.build()));

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
        is(ACCOUNT_MONEY));
    assertThat(remediesResponse.getCvv(), nullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Some text con dinero disponible en Mercado Pago"));
  }

  @Test
  public void applyRemedy_verify_custom_message_label_and_hybrid_case_credit_card() {

    CustomStringConfiguration customStringConfiguration = new CustomStringConfiguration();
    customStringConfiguration.setTotalDescriptionText("Some text       ");

    final BigDecimal totalAmount = new BigDecimal(100);

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123L, CALLER_ID_TEST, Site.MLA.name());
    when(remediesRequest.getCustomStringConfiguration()).thenReturn(customStringConfiguration);

    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("1234", "Santander", totalAmount, "back", 3);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn("credit_card");
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getPayerPaymentMethodRejected().getInstallments()).thenReturn(3);
    when(remediesRequest.getPayerPaymentMethodRejected().getBin()).thenReturn("407843");

    AlternativePayerPaymentMethod.AlternativePayerPaymentMethodBuilder
        alternativePayerPaymentMethodBuilder = AlternativePayerPaymentMethod.builder();
    alternativePayerPaymentMethodBuilder
        .customOptionId("1234567890")
        .paymentTypeId(ACCOUNT_MONEY)
        .escStatus("approved")
        .issuerName("BBVA")
        .esc(true)
        .bin("407843");

    final Installment installment = Mockito.mock(Installment.class);
    when(installment.getInstallments()).thenReturn(3);
    when(installment.getTotalAmount()).thenReturn(totalAmount);

    alternativePayerPaymentMethodBuilder.installmentsList(Collections.singletonList(installment));

    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(Collections.singletonList(alternativePayerPaymentMethodBuilder.build()));

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
        is(ACCOUNT_MONEY));
    assertThat(remediesResponse.getCvv(), nullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Some text con dinero disponible en Mercado Pago"));
  }

  @Test
  public void testBuildText_hybrid() {

    Context context =
        Context.builder().requestId(UUID.randomUUID().toString()).locale("es-AR").build();

    CustomStringConfiguration customStringConfiguration = new CustomStringConfiguration();
    customStringConfiguration.setTotalDescriptionText("Some text       ");

    final BigDecimal totalAmount = new BigDecimal(100);

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123L, CALLER_ID_TEST, Site.MLA.name());
    when(remediesRequest.getCustomStringConfiguration()).thenReturn(customStringConfiguration);

    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("1234", "Santander", totalAmount, "back", 3);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn(ACCOUNT_MONEY);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getPayerPaymentMethodRejected().getInstallments()).thenReturn(3);
    when(remediesRequest.getPayerPaymentMethodRejected().getBin()).thenReturn("678906");

    AlternativePayerPaymentMethod.AlternativePayerPaymentMethodBuilder
        alternativePayerPaymentMethodBuilder = AlternativePayerPaymentMethod.builder();
    alternativePayerPaymentMethodBuilder
        .customOptionId("1234567890")
        .paymentTypeId(DEBIT_CARD.name())
        .escStatus("approved")
        .issuerName("BBVA")
        .esc(true)
        .bin("678905");

    AlternativePayerPaymentMethod.AlternativePayerPaymentMethodBuilder
        alternativePayerPaymentMethodBuilder2 = AlternativePayerPaymentMethod.builder();
    alternativePayerPaymentMethodBuilder2
        .customOptionId("12345678901")
        .paymentTypeId(DEBIT_CARD.name())
        .escStatus("approved")
        .issuerName("BBVA")
        .esc(true)
        .bin("417401");

    List<AlternativePayerPaymentMethod> alternativePayerPaymentMethods = new ArrayList<>();
    alternativePayerPaymentMethods.add(alternativePayerPaymentMethodBuilder.build());
    alternativePayerPaymentMethods.add(alternativePayerPaymentMethodBuilder2.build());

    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(alternativePayerPaymentMethods);

    final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            remedyCvv, REMEDY_OTHER_REASON_TITLE, REMEDY_OTHER_REASON_MESSAGE);

    Text text =
        remedySuggestionPaymentMethod.buildText(
            context.getLocale(),
            customStringConfiguration,
            remediesRequest,
            alternativePayerPaymentMethodBuilder2.build());

    assertThat(text.getMessage(), is("Some text con crédito"));
  }

  @Test
  public void testBuildText_combo_card_debit() {

    Context context =
        Context.builder().requestId(UUID.randomUUID().toString()).locale("es-AR").build();

    CustomStringConfiguration customStringConfiguration = new CustomStringConfiguration();
    customStringConfiguration.setTotalDescriptionText("Some text       ");

    final BigDecimal totalAmount = new BigDecimal(100);

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123L, CALLER_ID_TEST, Site.MLA.name());
    when(remediesRequest.getCustomStringConfiguration()).thenReturn(customStringConfiguration);

    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("1234", "Santander", totalAmount, "back", 3);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn(ACCOUNT_MONEY);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getPayerPaymentMethodRejected().getInstallments()).thenReturn(3);
    when(remediesRequest.getPayerPaymentMethodRejected().getBin()).thenReturn("678906");

    AlternativePayerPaymentMethod.AlternativePayerPaymentMethodBuilder
        alternativePayerPaymentMethodBuilder = AlternativePayerPaymentMethod.builder();
    alternativePayerPaymentMethodBuilder
        .customOptionId("12345678903")
        .paymentTypeId(DEBIT_CARD.name())
        .escStatus("approved")
        .issuerName("BBVA")
        .esc(true)
        .bin("678905");

    AlternativePayerPaymentMethod.AlternativePayerPaymentMethodBuilder
        alternativePayerPaymentMethodBuilder2 = AlternativePayerPaymentMethod.builder();
    alternativePayerPaymentMethodBuilder2
        .customOptionId("12345678901")
        .paymentTypeId(DEBIT_CARD.name())
        .escStatus("approved")
        .issuerName("BBVA")
        .esc(true)
        .bin("417401");

    AlternativePayerPaymentMethod.AlternativePayerPaymentMethodBuilder
        alternativePayerPaymentMethodBuilder3 = AlternativePayerPaymentMethod.builder();
    alternativePayerPaymentMethodBuilder3
        .customOptionId("12345678903")
        .paymentTypeId(CREDIT_CARD.name())
        .escStatus("approved")
        .issuerName("BBVA")
        .esc(true)
        .bin("417401");

    List<AlternativePayerPaymentMethod> alternativePayerPaymentMethods = new ArrayList<>();
    alternativePayerPaymentMethods.add(alternativePayerPaymentMethodBuilder.build());
    alternativePayerPaymentMethods.add(alternativePayerPaymentMethodBuilder2.build());
    alternativePayerPaymentMethods.add(alternativePayerPaymentMethodBuilder3.build());

    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(alternativePayerPaymentMethods);

    final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            remedyCvv, REMEDY_OTHER_REASON_TITLE, REMEDY_OTHER_REASON_MESSAGE);

    Text text =
        remedySuggestionPaymentMethod.buildText(
            context.getLocale(),
            customStringConfiguration,
            remediesRequest,
            alternativePayerPaymentMethodBuilder.build());

    assertThat(text.getMessage(), is("Some text con débito"));
  }
}
