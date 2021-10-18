package com.mercadolibre.dto.instructions;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Interaction {

  private final Action action;
  private final String title;
  private final String content;
  private final boolean showMultilineContent;
}
