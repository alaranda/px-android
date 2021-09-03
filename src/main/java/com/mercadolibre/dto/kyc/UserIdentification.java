package com.mercadolibre.dto.kyc;

import com.mercadolibre.px.dto.lib.kyc.IdProvider;
import com.mercadolibre.px.dto.lib.user.Identification;
import lombok.Getter;

@Getter
public class UserIdentification implements IdProvider {

  private Long id;
  private Identification identification;
  private RegistrationIdentifiers registrationIdentifiers;

  @Override
  public Long getId() {
    return id;
  }

  public Email getRegistrationEmail() {
    return registrationIdentifiers == null ? null : registrationIdentifiers.getEmail();
  }
}
