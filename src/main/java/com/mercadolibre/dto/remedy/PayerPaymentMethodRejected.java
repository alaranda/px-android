package com.mercadolibre.dto.remedy;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayerPaymentMethodRejected {

  private String customOptionId;
  private String paymentMethodId;
  private String paymentTypeId;
  private String escStatus;
  private boolean esc;
  private String issuerName;
  private String lastFourDigit;
  private String securityCodeLocation;
  private int securityCodeLength;
  private int installments;
  private BigDecimal totalAmount;
}
