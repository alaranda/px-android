package com.mercadolibre.dto.kyc;

import com.mercadolibre.px.dto.lib.kyc.IdProvider;
import com.mercadolibre.px.dto.lib.user.Identification;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class UserIdentification implements IdProvider {

  private Long id;

  private Identification identification;

  private RegistrationIdentifiers registrationIdentifiers;

  private Person person;

  @Override
  public Long getId() {
    return id;
  }

  public Email getRegistrationEmail() {
    return registrationIdentifiers == null ? null : registrationIdentifiers.getEmail();
  }

  public List<Identification> getPersonOtherIdentifications() {
    if (getPerson() == null) {
      return new ArrayList<>();
    }
    return getPerson().getOtherIdentifications();
  }
}
