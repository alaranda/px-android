package com.mercadolibre.dto.instructions;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Reference {

  private final String label;
  private final List<String> fieldValue;
  private final String separator;
  @Deprecated private final String comment;
}
