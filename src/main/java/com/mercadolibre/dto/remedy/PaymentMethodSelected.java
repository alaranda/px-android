package com.mercadolibre.dto.remedy;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentMethodSelected {

  private AlternativePayerPaymentMethod alternativePayerPaymentMethod;
  private boolean remedyCvvRequired;
}
