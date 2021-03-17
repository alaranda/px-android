package com.mercadolibre.dto.remedy;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SuggestionPaymentMethodResponse {
  private final String title;
  private final String message;
  private final AlternativePayerPaymentMethod alternativePaymentMethod;
  private final Text bottomMessage;
}
