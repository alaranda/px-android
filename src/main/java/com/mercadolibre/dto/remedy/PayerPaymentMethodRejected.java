package com.mercadolibre.dto.remedy;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
  @Setter private BigDecimal totalAmount;
  private final String bin;
}
