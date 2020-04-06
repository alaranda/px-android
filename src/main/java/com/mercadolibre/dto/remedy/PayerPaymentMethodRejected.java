package com.mercadolibre.dto.remedy;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class PayerPaymentMethodRejected {

  private String paymentMethodId;
  private String paymentTypeId;
  private String issuerName;
  private String lastFourDigit;
  private String securityCodeLocation;
  private int securityCodeLength;
  private BigDecimal totalAmount;
  private int installments;
}
