package com.mercadolibre.dto;

import com.mercadolibre.px.dto.lib.user.Identification;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class User {

  private String id;
  private Long operatorId;
  private Identification identification;
  private String email;

  public User(final String id, final Long operatorIdCollector) {
    this.id = id;
    this.operatorId = operatorIdCollector;
  };
}
