package com.mercadolibre.helper;

import static org.mockito.Mockito.when;

import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import java.math.BigDecimal;
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
}
