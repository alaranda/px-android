package com.mercadolibre.dto.instructions;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Action {

  private final String label;
  private final String url;
  private final ActionTag tag;
  private final String content;
}
