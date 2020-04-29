package com.mercadolibre.dto.remedy;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class AlternativePayerPaymentMethod {

  private final String customOptionId;
  private final String paymentMethodId;
  private final String paymentTypeId;
  private final String escStatus;
  private boolean esc;
  private final String issuerName;
  private final String lastFourDigit;
  private int securityCodeLength;
  private final String securityCodeLocation;
  @Setter private Integer installments;
  @Setter private List<Installment> installmentsList;
}
