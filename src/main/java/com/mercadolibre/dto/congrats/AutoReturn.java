package com.mercadolibre.dto.congrats;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AutoReturn {

  private final String label;
  private final int seconds;
}
