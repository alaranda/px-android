package com.mercadolibre.dto.remedy;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayerPaymentMethodRejected {

  private final String customOptionId;
  private final String paymentMethodId;
  private final String paymentTypeId;
  private final String escStatus;
  private final boolean esc;
  private final String issuerName;
  private final String lastFourDigit;
  private final String securityCodeLocation;
  private final int securityCodeLength;
  private final int installments;
  private final BigDecimal totalAmount;
  private final String bin;
}
