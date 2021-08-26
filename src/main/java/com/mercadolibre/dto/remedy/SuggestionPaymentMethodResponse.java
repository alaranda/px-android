package com.mercadolibre.dto.remedy;

import com.mercadolibre.dto.modal.ModalAction;
import com.mercadolibre.px.dto.lib.text.Text;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SuggestionPaymentMethodResponse {
  private final String title;
  private final String message;
  private final AlternativePayerPaymentMethod alternativePaymentMethod;
  private final Text bottomMessage;
  private final Boolean shouldShowTinyCard;
  private final ModalAction modal;
}
