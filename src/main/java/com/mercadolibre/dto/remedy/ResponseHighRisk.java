package com.mercadolibre.dto.remedy;

import com.mercadolibre.dto.congrats.Action;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseHighRisk {

  private String title;
  private String message;
  private String deepLink;
  private Action actionLoud;
}
