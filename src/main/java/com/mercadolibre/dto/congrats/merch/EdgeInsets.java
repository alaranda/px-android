package com.mercadolibre.dto.congrats.merch;

public class EdgeInsets {

  private final double top;
  private final double bottom;
  private final double left;
  private final double right;

  public EdgeInsets(final double top, final double bottom, final double left, final double right) {
    this.top = top;
    this.bottom = bottom;
    this.left = left;
    this.right = right;
  }

  @Override
  public String toString() {
    return "EdgeInsets{"
        + "top="
        + top
        + ", bottom="
        + bottom
        + ", left="
        + left
        + ", right="
        + right
        + '}';
  }
}
