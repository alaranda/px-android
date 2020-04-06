package com.mercadolibre.dto.remedy;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseCvv {

  private final String title;
  private final String message;
  private final FieldSetting fieldSetting;
}
