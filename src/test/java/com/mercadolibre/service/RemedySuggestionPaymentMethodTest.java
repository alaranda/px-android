package com.mercadolibre.service;

import static com.mercadolibre.constants.Constants.STATUS_APPROVED;
import static com.mercadolibre.helper.MockTestHelper.*;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.ACCOUNT_MONEY;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.CONSUMER_CREDITS;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.CREDIT_CARD;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.DEBIT_CARD;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.DIGITAL_CURRENCY;
import static com.mercadolibre.utils.Translations.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.modal.ModalAction;
import com.mercadolibre.dto.remedy.*;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.context.UserAgent;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.dto.lib.text.Text;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.service.remedy.RemedyCvv;
import com.mercadolibre.service.remedy.RemedySuggestionPaymentMethod;
import com.mercadolibre.service.remedy.order.AccountMoneyRejected;
import com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes;
import com.mercadolibre.utils.SuggestionPaymentMehodsUtils;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;

public class RemedySuggestionPaymentMethodTest {

  private static final String CALLER_ID_TEST = "123456789";
  private static final Locale buildTextLocale = new Locale("es", "AR");
  final RemedyCvv remedyCvv = new RemedyCvv();
  private final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
      new RemedySuggestionPaymentMethod(
          remedyCvv, REMEDY_OTHER_REASON_TITLE, REMEDY_OTHER_REASON_MESSAGE);

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
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(CREDIT_CARD);
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

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());

    assertEquals(
        "El pago con Santander *** 1234 fue rechazado",
        remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Te sugerimos reintentar con este medio:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(CREDIT_CARD));
    assertNull(remediesResponse.getSuggestedPaymentMethod().getModal());
    assertEquals(
        CardSize.SMALL,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
    assertThat(remediesResponse.getCvv(), nullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar con crédito:"));
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
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(CREDIT_CARD);
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

    assertEquals(
        "Llegaste al límite de intentos de pago posibles.",
        remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Te sugerimos reintentar con este medio:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(CREDIT_CARD));
    assertNull(remediesResponse.getSuggestedPaymentMethod().getModal());
    assertEquals(
        CardSize.SMALL,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
    assertThat(remediesResponse.getCvv().getTitle(), notNullValue());
    assertThat(remediesResponse.getCvv().getMessage(), notNullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar con crédito:"));
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

    assertEquals(
        "Tu tarjeta está bloqueada.", remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Comunicate con tu banco para solucionarlo. Mientras tanto, te sugerimos reintentar con este medio:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(ACCOUNT_MONEY));
    assertNull(remediesResponse.getSuggestedPaymentMethod().getModal());
    assertEquals(
        CardSize.SMALL,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
    assertThat(remediesResponse.getCvv(), nullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar con dinero disponible en Mercado Pago:"));
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
    when(alternativePayerPaymentMethod.getPaymentTypeId()).thenReturn(DEBIT_CARD);
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

    assertEquals(
        "Tu tarjeta no acepta esta cantidad de cuotas.",
        remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Te sugerimos reintentar con este medio:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(DEBIT_CARD));
    assertNull(remediesResponse.getSuggestedPaymentMethod().getModal());
    assertEquals(
        CardSize.SMALL,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
    assertThat(remediesResponse.getCvv(), nullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar:"));
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
        mockPayerPaymentMethodRejected(CREDIT_CARD);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final List<AlternativePayerPaymentMethod> alternativePayerPaymentMethodList =
        mockAlternativePaymentMethodsList();
    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(alternativePayerPaymentMethodList);

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());

    assertEquals(
        "El pago con null *** null fue rechazado",
        remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Te sugerimos reintentar con este medio:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(CREDIT_CARD));
    assertNull(remediesResponse.getSuggestedPaymentMethod().getModal());
    assertEquals(
        CardSize.SMALL,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar con crédito:"));
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

    final Context context = mockContextLibDto();
    when(context.getUserAgent()).thenReturn(UserAgent.create("PX/iOS/4.36.4"));
    when(context.getLocale()).thenReturn(Locale.forLanguageTag("pt-BR"));

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(context, remediesRequest, new RemediesResponse());

    assertEquals(
        "Recusamos seu pagamento", remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Sugerimos que você tente novamente com este meio:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(CREDIT_CARD));
    assertNull(remediesResponse.getSuggestedPaymentMethod().getModal());
    assertEquals(
        CardSize.SMALL,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar com crédito:"));
  }

  @Test
  public void applyRemedy_rejectedTD_returnAM() {

    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getSiteId()).thenReturn("MLB");
    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethodRejected(DEBIT_CARD);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);

    final List<AlternativePayerPaymentMethod> alternativePayerPaymentMethodList =
        mockAlternativePaymentMethodsList();
    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(alternativePayerPaymentMethodList);

    final Context context = mockContextLibDto();
    when(context.getLocale()).thenReturn(Locale.forLanguageTag("pt-BR"));

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(context, remediesRequest, new RemediesResponse());

    assertEquals(
        "O pagamento com null *** null foi recusado",
        remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Sugerimos que você tente novamente com este meio:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(PaymentMethodsRejectedTypes.ACCOUNT_MONEY));
    assertNull(remediesResponse.getSuggestedPaymentMethod().getModal());
    assertEquals(
        CardSize.SMALL,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar:"));
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

    final Context context = mockContextLibDto();
    when(context.getUserAgent()).thenReturn(UserAgent.create("PX/iOS/4.36.0"));

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(context, remediesRequest, new RemediesResponse());

    assertEquals("Rechazamos tu pago", remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Te sugerimos reintentar con este medio:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(CREDIT_CARD));
    assertNull(remediesResponse.getSuggestedPaymentMethod().getModal());
    assertEquals(
        CardSize.SMALL,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar con crédito:"));
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

    final Context context = mockContextLibDto();
    when(context.getUserAgent()).thenReturn(UserAgent.create("PX/iOS/4.37.5"));

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(context, remediesRequest, new RemediesResponse());

    assertEquals("Rechazamos tu pago", remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Te sugerimos reintentar con este medio:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(CREDIT_CARD));
    assertNull(remediesResponse.getSuggestedPaymentMethod().getModal());
    assertEquals(
        CardSize.SMALL,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar con crédito:"));
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

    final Context context = mockContextLibDto();
    when(context.getUserAgent()).thenReturn(UserAgent.create("PX/iOS/4.37.5"));
    when(context.getLocale()).thenReturn(Locale.forLanguageTag("pt-BR"));

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(context, remediesRequest, new RemediesResponse());

    assertEquals(
        "Recusamos seu pagamento", remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Sugerimos que você tente novamente com este meio:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(CREDIT_CARD));
    assertNull(remediesResponse.getSuggestedPaymentMethod().getModal());
    assertEquals(
        CardSize.SMALL,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
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

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());

    assertEquals(
        "El pago con Santander *** 1234 fue rechazado",
        remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Te sugerimos reintentar con este medio:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(ACCOUNT_MONEY));
    assertNull(remediesResponse.getSuggestedPaymentMethod().getModal());
    assertEquals(
        CardSize.SMALL,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
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

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());

    assertEquals(
        "El pago con Santander *** 1234 fue rechazado",
        remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Te sugerimos reintentar con este medio:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(ACCOUNT_MONEY));
    assertNull(remediesResponse.getSuggestedPaymentMethod().getModal());
    assertEquals(
        CardSize.SMALL,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
    assertThat(remediesResponse.getCvv(), nullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Some text con dinero disponible en Mercado Pago"));
  }

  @Test
  public void applyRemedy_statusDetailBlacklist_remedySuggestedPmConsumerCredits()
      throws NoSuchFieldException {

    final BigDecimal totalAmount = new BigDecimal(100);

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123L, CALLER_ID_TEST, Site.MLA.name());
    when(remediesRequest.getCustomStringConfiguration()).thenReturn(null);

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
        .customOptionId(CONSUMER_CREDITS)
        .paymentMethodId(CONSUMER_CREDITS)
        .paymentTypeId(DIGITAL_CURRENCY)
        .escStatus("approved");

    final Installment installment = Mockito.mock(Installment.class);
    when(installment.getInstallments()).thenReturn(3);
    when(installment.getTotalAmount()).thenReturn(totalAmount);

    alternativePayerPaymentMethodBuilder.installmentsList(Collections.singletonList(installment));
    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(Collections.singletonList(alternativePayerPaymentMethodBuilder.build()));

    RemedySuggestionPaymentMethod remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            remedyCvv, REMEDY_OTHER_REASON_TITLE, REMEDY_OTHER_REASON_MESSAGE);

    // TODO remove this mocking process till line 746 once Consumer Credits is enabled
    PaymentMethodsRejectedTypes mockPaymentMethodsRejectedTypes =
        mock(PaymentMethodsRejectedTypes.class);
    AccountMoneyRejected mockAccountMoneyRejected = mock(AccountMoneyRejected.class);
    when(mockPaymentMethodsRejectedTypes.getSuggestionOrderCriteria(
            payerPaymentMethodRejected.getPaymentTypeId()))
        .thenReturn(mockAccountMoneyRejected);
    when(mockAccountMoneyRejected.findBestMedium(any(), any()))
        .thenReturn(
            SuggestionPaymentMehodsUtils.getPaymentMethodSelected(
                Arrays.asList(alternativePayerPaymentMethodBuilder.build())));
    Field field =
        RemedySuggestionPaymentMethod.class.getDeclaredField("paymentMethodsRejectedTypes");
    FieldSetter.setField(remedySuggestionPaymentMethod, field, mockPaymentMethodsRejectedTypes);

    final RemediesResponse remediesResponse =
        remedySuggestionPaymentMethod.applyRemedy(
            mockContextLibDto(), remediesRequest, new RemediesResponse());

    assertEquals(
        "El pago con Santander *** 1234 fue rechazado",
        remediesResponse.getSuggestedPaymentMethod().getTitle());
    assertEquals(
        "Puedes usar tu línea de crédito para pagar sin tarjeta:",
        remediesResponse.getSuggestedPaymentMethod().getMessage());
    assertThat(
        remediesResponse
            .getSuggestedPaymentMethod()
            .getAlternativePaymentMethod()
            .getPaymentTypeId(),
        is(DIGITAL_CURRENCY));

    ModalAction modalAction = remediesResponse.getSuggestedPaymentMethod().getModal();
    assertNotNull(modalAction);
    assertNotNull(modalAction);
    assertEquals("Recuerda que usarás Mercado Crédito", modalAction.getTitle().getMessage());
    assertEquals(Constants.WEIGHT_BOLD, modalAction.getTitle().getWeight());
    assertEquals(
        Constants.CONSUMER_CREDITS_MODAL_TEXT_COLOR, modalAction.getTitle().getTextColor());
    assertNull(modalAction.getTitle().getBackgroundColor());
    assertEquals(
        "Las cuotas tienen un valor fijo y podrás pagarlas desde tu cuenta de Mercado Pago.",
        modalAction.getDescription().getMessage());
    assertEquals(Constants.WEIGHT_BOLD, modalAction.getDescription().getWeight());
    assertEquals(
        Constants.CONSUMER_CREDITS_MODAL_TEXT_COLOR, modalAction.getDescription().getTextColor());
    assertNull(modalAction.getDescription().getBackgroundColor());
    assertEquals("Confirmar pago", modalAction.getMainButton().getLabel());
    assertEquals(Constants.ACTION_PAY, modalAction.getMainButton().getAction());
    assertEquals(Constants.BUTTON_LOUD, modalAction.getMainButton().getType());
    assertEquals("Pagar de otra forma", modalAction.getSecondaryButton().getLabel());
    assertEquals(Constants.ACTION_CHANGE_PM, modalAction.getSecondaryButton().getAction());
    assertEquals(Constants.BUTTON_QUIET, modalAction.getSecondaryButton().getType());

    assertEquals(
        CardSize.MINI,
        remediesResponse.getSuggestedPaymentMethod().getAlternativePaymentMethod().getCardSize());
    assertThat(remediesResponse.getCvv(), nullValue());
    assertThat(remediesResponse.getSuggestedPaymentMethod().getBottomMessage(), notNullValue());
    assertThat(
        remediesResponse.getSuggestedPaymentMethod().getBottomMessage().getMessage(),
        is("Total a pagar:"));
  }

  @Test
  public void testBuildText_hybrid() {

    Context context =
        Context.builder().requestId(UUID.randomUUID().toString()).locale("es-AR").build();

    final RemediesRequest remediesRequest = getRemediesRequest(ACCOUNT_MONEY, false, false, true);

    Text text =
        remedySuggestionPaymentMethod.buildText(
            context.getLocale(),
            remediesRequest,
            remediesRequest.getAlternativePayerPaymentMethods().get(1));

    assertThat(text.getMessage(), is("Some text con crédito"));
  }

  @Test
  public void testBuildText_combo_card_debit() {

    Context context =
        Context.builder().requestId(UUID.randomUUID().toString()).locale("es-AR").build();

    final RemediesRequest remediesRequest = getRemediesRequest(ACCOUNT_MONEY, true, false, true);

    Text text =
        remedySuggestionPaymentMethod.buildText(
            context.getLocale(),
            remediesRequest,
            remediesRequest.getAlternativePayerPaymentMethods().get(0));

    assertThat(text.getMessage(), is("Some text con débito"));
  }

  @Test
  public void testBuildText_alternativePayerPaymentMethodAccountMoney_hybridCard() {
    testBuildText_alternativePayerPaymentMethodAccountMoney_hybridCard(false);
    testBuildText_alternativePayerPaymentMethodAccountMoney_hybridCard(true);
  }

  private void testBuildText_alternativePayerPaymentMethodAccountMoney_hybridCard(
      boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(ACCOUNT_MONEY, false, true, isCustomStringConfiguration);

    AlternativePayerPaymentMethod creditCardHybridBin =
        remediesRequest.getAlternativePayerPaymentMethods().get(1);

    Text text =
        remedySuggestionPaymentMethod.buildText(
            buildTextLocale, remediesRequest, creditCardHybridBin);

    if (isCustomStringConfiguration) {
      assertText("Some text con crédito", text);
    } else {
      assertText("Total a pagar con crédito:", text);
    }
  }

  @Test
  public void testBuildText_rejectedAccountMoney_hybridCard() {
    testBuildText_rejectedAccountMoney_hybridCard(false);
    testBuildText_rejectedAccountMoney_hybridCard(true);
  }

  private void testBuildText_rejectedAccountMoney_hybridCard(boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(ACCOUNT_MONEY, false, false, isCustomStringConfiguration);

    AlternativePayerPaymentMethod creditCardHybridBin =
        remediesRequest.getAlternativePayerPaymentMethods().get(1);

    Text text =
        remedySuggestionPaymentMethod.buildText(
            buildTextLocale, remediesRequest, creditCardHybridBin);

    if (isCustomStringConfiguration) {
      assertText("Some text con crédito", text);
    } else {
      assertText("Total a pagar con crédito:", text);
    }
  }

  @Test
  public void testBuildText_rejectedCreditCard_rejectedNotHybridCard() {
    testBuildText_rejectedCreditCard_rejectedNotHybridCard(false);
    testBuildText_rejectedCreditCard_rejectedNotHybridCard(true);
  }

  private void testBuildText_rejectedCreditCard_rejectedNotHybridCard(
      boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(CREDIT_CARD, false, false, isCustomStringConfiguration);

    AlternativePayerPaymentMethod creditCardHybridBin =
        remediesRequest.getAlternativePayerPaymentMethods().get(1);

    Text text =
        remedySuggestionPaymentMethod.buildText(
            buildTextLocale, remediesRequest, creditCardHybridBin);
    if (isCustomStringConfiguration) {
      assertText("Some text", text);
    } else {
      assertText("Total a pagar:", text);
    }
  }

  @Test
  public void testBuildText_rejectedCreditCard_rejectedHybridCard_withAccountMoneyAlternative() {
    testBuildText_rejectedCreditCard_rejectedHybridCard_withAccountMoneyAlternative(false);
    testBuildText_rejectedCreditCard_rejectedHybridCard_withAccountMoneyAlternative(true);
  }

  private void testBuildText_rejectedCreditCard_rejectedHybridCard_withAccountMoneyAlternative(
      boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(CREDIT_CARD, false, true, isCustomStringConfiguration);

    when(remediesRequest.getPayerPaymentMethodRejected().getBin()).thenReturn("417401");

    AlternativePayerPaymentMethod accountMoneyAlternative =
        remediesRequest.getAlternativePayerPaymentMethods().get(2);

    Text text =
        remedySuggestionPaymentMethod.buildText(
            buildTextLocale, remediesRequest, accountMoneyAlternative);

    if (isCustomStringConfiguration) {
      assertText("Some text con dinero disponible en Mercado Pago", text);
    } else {
      assertText("Total a pagar con dinero disponible en Mercado Pago:", text);
    }
  }

  @Test
  public void testBuildText_rejectedDebitCard_rejectedHybridCard_withAccountMoneyAlternative() {
    testBuildText_rejectedDebitCard_rejectedHybridCard_withAccountMoneyAlternative(false);
    testBuildText_rejectedDebitCard_rejectedHybridCard_withAccountMoneyAlternative(true);
  }

  private void testBuildText_rejectedDebitCard_rejectedHybridCard_withAccountMoneyAlternative(
      boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(DEBIT_CARD, false, true, isCustomStringConfiguration);

    when(remediesRequest.getPayerPaymentMethodRejected().getBin()).thenReturn("417401");

    AlternativePayerPaymentMethod accountMoneyAlternative =
        remediesRequest.getAlternativePayerPaymentMethods().get(2);

    Text text =
        remedySuggestionPaymentMethod.buildText(
            buildTextLocale, remediesRequest, accountMoneyAlternative);
    if (isCustomStringConfiguration) {
      assertText("Some text con dinero disponible en Mercado Pago", text);
    } else {
      assertText("Total a pagar con dinero disponible en Mercado Pago:", text);
    }
  }

  @Test
  public void
      testBuildText_rejectedDebitCard_rejectedNotHybridCard_hybridCardAndAccountMoneyAlternatives() {
    testBuildText_rejectedDebitCard_rejectedNotHybridCard_hybridCardAndAccountMoneyAlternatives(
        false);
    testBuildText_rejectedDebitCard_rejectedNotHybridCard_hybridCardAndAccountMoneyAlternatives(
        true);
  }

  private void
      testBuildText_rejectedDebitCard_rejectedNotHybridCard_hybridCardAndAccountMoneyAlternatives(
          boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(DEBIT_CARD, false, true, isCustomStringConfiguration);

    AlternativePayerPaymentMethod accountMoneyAlternative =
        remediesRequest.getAlternativePayerPaymentMethods().get(2);

    Text text =
        remedySuggestionPaymentMethod.buildText(
            buildTextLocale, remediesRequest, accountMoneyAlternative);
    if (isCustomStringConfiguration) {
      assertText("Some text con dinero disponible en Mercado Pago", text);
    } else {
      assertText("Total a pagar con dinero disponible en Mercado Pago:", text);
    }
  }

  @Test
  public void
      testBuildText_rejectedDebitCard_rejectedNotHybridCard_accountMoneyNoHybridCardAlternatives() {
    testBuildText_rejectedDebitCard_rejectedNotHybridCard_accountMoneyNoHybridCardAlternatives(
        false);
    testBuildText_rejectedDebitCard_rejectedNotHybridCard_accountMoneyNoHybridCardAlternatives(
        true);
  }

  private void
      testBuildText_rejectedDebitCard_rejectedNotHybridCard_accountMoneyNoHybridCardAlternatives(
          boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(DEBIT_CARD, false, true, isCustomStringConfiguration);

    AlternativePayerPaymentMethod accountMoneyAlternative =
        remediesRequest.getAlternativePayerPaymentMethods().get(2);

    remediesRequest.getAlternativePayerPaymentMethods().remove(1);

    Text text =
        remedySuggestionPaymentMethod.buildText(
            buildTextLocale, remediesRequest, accountMoneyAlternative);

    if (isCustomStringConfiguration) {
      assertText("Some text", text);
    } else {
      assertText("Total a pagar:", text);
    }
  }

  @Test
  public void testBuildText_rejectedCreditCardCombo_rejectedNotHybridCard() {
    testBuildText_rejectedCreditCardCombo_rejectedNotHybridCard(false);
    testBuildText_rejectedCreditCardCombo_rejectedNotHybridCard(true);
  }

  private void testBuildText_rejectedCreditCardCombo_rejectedNotHybridCard(
      boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(CREDIT_CARD, false, false, isCustomStringConfiguration);

    AlternativePayerPaymentMethod comboDebitCard =
        remediesRequest.getAlternativePayerPaymentMethods().get(0);

    when(remediesRequest.getPayerPaymentMethodRejected().getCustomOptionId())
        .thenReturn(comboDebitCard.getCustomOptionId());

    Text text =
        remedySuggestionPaymentMethod.buildText(buildTextLocale, remediesRequest, comboDebitCard);
    if (isCustomStringConfiguration) {
      assertText("Some text con débito", text);
    } else {
      assertText("Total a pagar con débito:", text);
    }
  }

  @Test
  public void testBuildText_rejectedDebitCardCombo_rejectedNotHybridCard() {
    testBuildText_rejectedDebitCardCombo_rejectedNotHybridCard(false);
    testBuildText_rejectedDebitCardCombo_rejectedNotHybridCard(true);
  }

  private void testBuildText_rejectedDebitCardCombo_rejectedNotHybridCard(
      boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(DEBIT_CARD, false, false, isCustomStringConfiguration);

    AlternativePayerPaymentMethod comboCreditCard =
        AlternativePayerPaymentMethod.builder()
            .customOptionId("12345678903")
            .paymentTypeId(CREDIT_CARD)
            .escStatus("approved")
            .issuerName("BBVA")
            .esc(true)
            .bin("678905")
            .build();

    when(remediesRequest.getPayerPaymentMethodRejected().getCustomOptionId())
        .thenReturn(comboCreditCard.getCustomOptionId());

    Text text =
        remedySuggestionPaymentMethod.buildText(buildTextLocale, remediesRequest, comboCreditCard);
    if (isCustomStringConfiguration) {
      assertText("Some text con crédito", text);
    } else {
      assertText("Total a pagar con crédito:", text);
    }
  }

  @Test
  public void testBuildText_rejectedCreditCard_rejectedNotHybridCard_comboDebitCardAlternative() {
    testBuildText_rejectedCreditCard_rejectedNotHybridCard_comboDebitCardAlternative(false);
    testBuildText_rejectedCreditCard_rejectedNotHybridCard_comboDebitCardAlternative(true);
  }

  private void testBuildText_rejectedCreditCard_rejectedNotHybridCard_comboDebitCardAlternative(
      boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(CREDIT_CARD, true, false, isCustomStringConfiguration);

    AlternativePayerPaymentMethod comboDebitCard =
        remediesRequest.getAlternativePayerPaymentMethods().get(0);

    Text text =
        remedySuggestionPaymentMethod.buildText(buildTextLocale, remediesRequest, comboDebitCard);

    if (isCustomStringConfiguration) {
      assertText("Some text con débito", text);
    } else {
      assertText("Total a pagar con débito:", text);
    }
  }

  @Test
  public void testBuildText_rejectedCreditCard_rejectedNotHybridCard_comboCreditCardAlternative() {
    testBuildText_rejectedCreditCard_rejectedNotHybridCard_comboCreditCardAlternative(false);
    testBuildText_rejectedCreditCard_rejectedNotHybridCard_comboCreditCardAlternative(true);
  }

  private void testBuildText_rejectedCreditCard_rejectedNotHybridCard_comboCreditCardAlternative(
      boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(CREDIT_CARD, true, false, isCustomStringConfiguration);

    AlternativePayerPaymentMethod comboCreditCard =
        remediesRequest.getAlternativePayerPaymentMethods().get(2);

    Text text =
        remedySuggestionPaymentMethod.buildText(buildTextLocale, remediesRequest, comboCreditCard);

    if (isCustomStringConfiguration) {
      assertText("Some text con crédito", text);
    } else {
      assertText("Total a pagar con crédito:", text);
    }
  }

  @Test
  public void testBuildText_rejectedDebitCard_rejectedNotHybridCard_comboDebitCardAlternative() {
    testBuildText_rejectedDebitCard_rejectedNotHybridCard_comboDebitCardAlternative(false);
    testBuildText_rejectedDebitCard_rejectedNotHybridCard_comboDebitCardAlternative(true);
  }

  private void testBuildText_rejectedDebitCard_rejectedNotHybridCard_comboDebitCardAlternative(
      boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(DEBIT_CARD, true, false, isCustomStringConfiguration);

    AlternativePayerPaymentMethod comboDebitCard =
        remediesRequest.getAlternativePayerPaymentMethods().get(0);

    Text text =
        remedySuggestionPaymentMethod.buildText(buildTextLocale, remediesRequest, comboDebitCard);

    if (isCustomStringConfiguration) {
      assertText("Some text con débito", text);
    } else {
      assertText("Total a pagar con débito:", text);
    }
  }

  @Test
  public void testBuildText_rejectedDebitCard_rejectedNotHybridCard_comboCreditCardAlternative() {
    testBuildText_rejectedDebitCard_rejectedNotHybridCard_comboCreditCardAlternative(false);
    testBuildText_rejectedDebitCard_rejectedNotHybridCard_comboCreditCardAlternative(true);
  }

  private void testBuildText_rejectedDebitCard_rejectedNotHybridCard_comboCreditCardAlternative(
      boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(DEBIT_CARD, true, false, isCustomStringConfiguration);

    AlternativePayerPaymentMethod comboCreditCard =
        remediesRequest.getAlternativePayerPaymentMethods().get(2);

    Text text =
        remedySuggestionPaymentMethod.buildText(buildTextLocale, remediesRequest, comboCreditCard);

    if (isCustomStringConfiguration) {
      assertText("Some text con crédito", text);
    } else {
      assertText("Total a pagar con crédito:", text);
    }
  }

  @Test
  public void testBuildText_rejectedDebitCard_rejectedNotHybridCard_noComboAlternative() {
    testBuildText_rejectedDebitCard_rejectedNotHybridCard_noComboAlternative(false);
    testBuildText_rejectedDebitCard_rejectedNotHybridCard_noComboAlternative(true);
  }

  private void testBuildText_rejectedDebitCard_rejectedNotHybridCard_noComboAlternative(
      boolean isCustomStringConfiguration) {
    final RemediesRequest remediesRequest =
        getRemediesRequest(DEBIT_CARD, false, false, isCustomStringConfiguration);

    AlternativePayerPaymentMethod debitCard =
        remediesRequest.getAlternativePayerPaymentMethods().get(0);

    Text text =
        remedySuggestionPaymentMethod.buildText(buildTextLocale, remediesRequest, debitCard);

    if (isCustomStringConfiguration) {
      assertText("Some text", text);
    } else {
      assertText("Total a pagar:", text);
    }
  }

  private void assertText(String message, Text text) {
    assertEquals(message, text.getMessage());
    assertEquals(Constants.WHITE_COLOR, text.getBackgroundColor());
    assertEquals(Constants.BLACK_COLOR, text.getTextColor());
    assertEquals(Constants.WEIGHT_SEMI_BOLD, text.getWeight());
  }

  private RemediesRequest getRemediesRequest(
      String rejectedPaymentTypeId,
      boolean addComboCreditCardAlternative,
      boolean addAccountMoneyAlternative,
      boolean isCustomStringConfiguration) {
    final BigDecimal totalAmount = new BigDecimal(100);

    final RemediesRequest remediesRequest =
        mockRemediesRequest(123L, CALLER_ID_TEST, Site.MLA.name());

    if (isCustomStringConfiguration) {
      CustomStringConfiguration customStringConfiguration = new CustomStringConfiguration();
      customStringConfiguration.setTotalDescriptionText("Some text       ");
      when(remediesRequest.getCustomStringConfiguration()).thenReturn(customStringConfiguration);
    }

    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        mockPayerPaymentMethod("1234", "Santander", totalAmount, "back", 3);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn(rejectedPaymentTypeId);
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
    when(remediesRequest.isOneTap()).thenReturn(true);
    when(remediesRequest.getPayerPaymentMethodRejected().getInstallments()).thenReturn(3);
    when(remediesRequest.getPayerPaymentMethodRejected().getBin()).thenReturn("678906");

    List<AlternativePayerPaymentMethod> alternativePayerPaymentMethods =
        getAlternativePayerPaymentMethods(
            addComboCreditCardAlternative, addAccountMoneyAlternative);

    when(remediesRequest.getAlternativePayerPaymentMethods())
        .thenReturn(alternativePayerPaymentMethods);

    return remediesRequest;
  }

  private List<AlternativePayerPaymentMethod> getAlternativePayerPaymentMethods(
      boolean addComboCreditCardAlternative, boolean addAccountMoneyAlternative) {
    List<AlternativePayerPaymentMethod> alternativePayerPaymentMethods = new LinkedList<>();

    AlternativePayerPaymentMethod.AlternativePayerPaymentMethodBuilder
        alternativePayerPaymentMethodBuilder =
            AlternativePayerPaymentMethod.builder()
                .customOptionId("12345678903")
                .paymentTypeId(DEBIT_CARD)
                .escStatus("approved")
                .issuerName("BBVA")
                .esc(true)
                .bin("678905");
    alternativePayerPaymentMethods.add(alternativePayerPaymentMethodBuilder.build());

    alternativePayerPaymentMethodBuilder =
        AlternativePayerPaymentMethod.builder()
            .customOptionId("12345678901")
            .paymentTypeId(DEBIT_CARD)
            .escStatus("approved")
            .issuerName("BBVA")
            .esc(true)
            .bin("417401");
    alternativePayerPaymentMethods.add(alternativePayerPaymentMethodBuilder.build());

    if (addComboCreditCardAlternative) {
      alternativePayerPaymentMethodBuilder =
          AlternativePayerPaymentMethod.builder()
              .customOptionId("12345678903")
              .paymentTypeId(CREDIT_CARD)
              .escStatus("approved")
              .issuerName("BBVA")
              .esc(true)
              .bin("678905");
      alternativePayerPaymentMethods.add(alternativePayerPaymentMethodBuilder.build());
    }

    if (addAccountMoneyAlternative) {
      alternativePayerPaymentMethodBuilder =
          AlternativePayerPaymentMethod.builder()
              .customOptionId(ACCOUNT_MONEY)
              .paymentTypeId(ACCOUNT_MONEY)
              .paymentMethodId(ACCOUNT_MONEY)
              .escStatus("not_available")
              .esc(false);
      alternativePayerPaymentMethods.add(alternativePayerPaymentMethodBuilder.build());
    }

    return alternativePayerPaymentMethods;
  }
}
