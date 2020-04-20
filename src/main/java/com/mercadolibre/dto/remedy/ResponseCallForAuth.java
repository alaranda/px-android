package com.mercadolibre.dto.remedy;

import com.mercadolibre.dto.congrats.Action;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseCallForAuth {

  private String title;
  private String message;
  private Action actionLoud;
}
