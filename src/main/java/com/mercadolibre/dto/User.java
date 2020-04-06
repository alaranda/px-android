package com.mercadolibre.dto;

import com.mercadolibre.px.dto.lib.user.Identification;

public final class User {

  private String id;
  private Identification identification;
  private String email;

  public User(final String id) {
    this.id = id;
  };

  public String getId() {
    return id;
  }

  public Identification getIdentification() {
    return identification;
  }

  public String getEmail() {
    return email;
  }
}
