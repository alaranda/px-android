package com.mercadolibre.dto.congrats;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperationInfoType {
  NEUTRAL("neutral");

  private String value;
}
