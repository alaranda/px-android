package com.mercadolibre.dto.congrats;

public class ActionDownload {

  private String title;
  private Action action;

  public ActionDownload(final String title, final Action action) {
    this.title = title;
    this.action = action;
  }

  public String toString() {
    return String.format("ActionDownload{Title=[%s], Action=[%s]}", title, action.toString());
  }
}
