package com.mercadolibre.dto.remedy;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SuggestionPaymentMethodResponse {

  private String title;
  private String message;
  private AlternativePayerPaymentMethod alternativePayerPaymentMethod;
}
