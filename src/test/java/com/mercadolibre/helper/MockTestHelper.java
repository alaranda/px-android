package com.mercadolibre.helper;

import static com.mercadolibre.constants.Constants.STATUS_APPROVED;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.ACCOUNT_MONEY;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.CONSUMER_CREDITS;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.CREDIT_CARD;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.DEBIT_CARD;
import static org.mockito.Mockito.when;

import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.Installment;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.mockito.Mockito;

public class MockTestHelper {

  public static final Context CONTEXT_ES =
      Context.builder()
          .requestId(UUID.randomUUID().toString())
          .locale("es-AR")
          .platform(Platform.MP)
          .build();

  public static RemediesRequest mockRemediesRequest(
      final Long riskId, final String callerId, final String site) {
    final RemediesRequest remediesRequest = Mockito.mock(RemediesRequest.class);
    when(remediesRequest.getUserAgent()).thenReturn(UserAgent.create("PX/Android/4.40.0"));
    when(remediesRequest.getRiskExcecutionId()).thenReturn(riskId);
    when(remediesRequest.getUserId()).thenReturn(callerId);
    when(remediesRequest.getSiteId()).thenReturn(site);
    PayerPaymentMethodRejected payerPaymentMethodRejected =
        Mockito.mock(PayerPaymentMethodRejected.class);
    when(payerPaymentMethodRejected.getPaymentMethodId()).thenReturn("visa");
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn("credit_card");
    when(remediesRequest.getPayerPaymentMethodRejected()).thenReturn(payerPaymentMethodRejected);
    return remediesRequest;
  }

  public static PayerPaymentMethodRejected mockPayerPaymentMethod(
      final String lastFourDigit,
      final String issuerName,
      final BigDecimal totalAmount,
      final String securityCodeLocation,
      final int securityCodeLength) {

    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        Mockito.mock(PayerPaymentMethodRejected.class);
    when(payerPaymentMethodRejected.getPaymentMethodId()).thenReturn("visa");
    when(payerPaymentMethodRejected.getLastFourDigit()).thenReturn(lastFourDigit);
    when(payerPaymentMethodRejected.getIssuerName()).thenReturn(issuerName);
    when(payerPaymentMethodRejected.getTotalAmount()).thenReturn(totalAmount);
    when(payerPaymentMethodRejected.getSecurityCodeLocation()).thenReturn(securityCodeLocation);
    when(payerPaymentMethodRejected.getSecurityCodeLength()).thenReturn(securityCodeLength);
    return payerPaymentMethodRejected;
  }

  public static List<AlternativePayerPaymentMethod> mockAlternativePaymentMethodsList() {

    AlternativePayerPaymentMethod tcEsc = Mockito.mock(AlternativePayerPaymentMethod.class);
    when(tcEsc.getPaymentTypeId()).thenReturn(CREDIT_CARD);
    when(tcEsc.getEscStatus()).thenReturn(STATUS_APPROVED);

    AlternativePayerPaymentMethod tcNotEsc = Mockito.mock(AlternativePayerPaymentMethod.class);
    when(tcNotEsc.getPaymentTypeId()).thenReturn(CREDIT_CARD);
    when(tcNotEsc.getEscStatus()).thenReturn("rejected");

    AlternativePayerPaymentMethod tdEsc = Mockito.mock(AlternativePayerPaymentMethod.class);
    when(tdEsc.getPaymentTypeId()).thenReturn(DEBIT_CARD);
    when(tdEsc.getEscStatus()).thenReturn(STATUS_APPROVED);

    AlternativePayerPaymentMethod tdNotEsc = Mockito.mock(AlternativePayerPaymentMethod.class);
    when(tdNotEsc.getPaymentTypeId()).thenReturn(DEBIT_CARD);
    when(tdNotEsc.getEscStatus()).thenReturn("rejected");

    AlternativePayerPaymentMethod am = Mockito.mock(AlternativePayerPaymentMethod.class);
    when(am.getPaymentTypeId()).thenReturn(ACCOUNT_MONEY);

    AlternativePayerPaymentMethod cc = Mockito.mock(AlternativePayerPaymentMethod.class);
    when(cc.getPaymentTypeId()).thenReturn(CONSUMER_CREDITS);

    Installment installment_one = Mockito.mock(Installment.class);
    when(installment_one.getInstallments()).thenReturn(1);
    when(installment_one.getTotalAmount()).thenReturn(new BigDecimal(123));

    Installment installment_three = Mockito.mock(Installment.class);
    when(installment_three.getInstallments()).thenReturn(3);
    when(installment_three.getTotalAmount()).thenReturn(new BigDecimal(456));

    Installment installment_six = Mockito.mock(Installment.class);
    when(installment_six.getInstallments()).thenReturn(6);
    when(installment_six.getTotalAmount()).thenReturn(new BigDecimal(789));

    final List<Installment> installments = new ArrayList<>();
    installments.add(installment_one);
    installments.add(installment_three);
    installments.add(installment_six);

    when(tcEsc.getInstallmentsList()).thenReturn(installments);
    when(tcNotEsc.getInstallmentsList()).thenReturn(installments);
    when(cc.getInstallmentsList()).thenReturn(installments);

    final List<AlternativePayerPaymentMethod> alternativePayerPaymentMethodList = new ArrayList<>();
    alternativePayerPaymentMethodList.add(tcEsc);
    alternativePayerPaymentMethodList.add(tcNotEsc);
    alternativePayerPaymentMethodList.add(tdEsc);
    alternativePayerPaymentMethodList.add(tdNotEsc);
    alternativePayerPaymentMethodList.add(am);
    alternativePayerPaymentMethodList.add(cc);

    return alternativePayerPaymentMethodList;
  }

  public static PayerPaymentMethodRejected mockPayerPaymentMethodRejected(
      final String paymentTypeId) {

    PayerPaymentMethodRejected payerPaymentMethodRejected =
        Mockito.mock(PayerPaymentMethodRejected.class);
    when(payerPaymentMethodRejected.getPaymentTypeId()).thenReturn(paymentTypeId);
    when(payerPaymentMethodRejected.getInstallments()).thenReturn(3);
    when(payerPaymentMethodRejected.getTotalAmount()).thenReturn(new BigDecimal(123));

    return payerPaymentMethodRejected;
  }
}
