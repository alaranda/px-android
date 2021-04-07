package com.mercadolibre.dto.congrats;

import java.math.BigDecimal;

public class PointsProgress {

  private BigDecimal percentage;
  private String levelColor;
  private Integer levelNumber;

  public PointsProgress() {}

  public String toString() {
    return String.format(
        "PointsProgress{percentage=[%s], levelColor=[%s], levelNumber=[%s]}",
        percentage.toString(), levelColor, levelNumber.toString());
  }
}
