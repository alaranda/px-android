package com.mercadolibre.dto.payment;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class TransactionDetails {
  private String externalResourceUrl;
  private String financialInstitution;
  private String paymentMethodReferenceId;
  private BigDecimal totalPaidAmount;
  private String verificationCode;
}
