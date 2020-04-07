package com.mercadolibre.dto.congrats;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Action {

  private String label;
  private String target;
  private String mlTarget;
  private String mpTarget;

  public Action(final String label, final String target) {
    this.label = label;
    this.target = target;
  }

  public Action(final String label) {
    this(label, null);
  }

  public String toString() {
    return String.format(
        "Action{label=[%s], target=[%s], mlTarget=[%s], mpTarget=[%s]}",
        label, target, mlTarget, mpTarget);
  }
}
