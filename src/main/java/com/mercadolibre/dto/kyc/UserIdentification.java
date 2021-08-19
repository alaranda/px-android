package com.mercadolibre.dto.kyc;

import com.mercadolibre.px.dto.lib.kyc.IdProvider;
import com.mercadolibre.px.dto.lib.user.Identification;
import lombok.Getter;

@Getter
public class UserIdentification implements IdProvider {

  private Long id;
  private Identification identification;

  @Override
  public Long getId() {
    return id;
  }
}
