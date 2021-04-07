package com.mercadolibre.dto.cha;

import lombok.Getter;

@Getter
public class Currency {

  private String id;
  private Integer decimalPlaces;
  private Character decimalSeparator;
  private Character thousandsSeparator;
}
