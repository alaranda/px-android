package com.mercadolibre.dto.remedy;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseRemedyDefault {

  private final String title;
  private final String message;
}
