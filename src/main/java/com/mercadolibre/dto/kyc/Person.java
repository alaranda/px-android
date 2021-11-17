package com.mercadolibre.dto.kyc;

import com.mercadolibre.px.dto.lib.user.Identification;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/** The KyC Person type class. */
@Getter
public class Person {

  private List<Identification> otherIdentifications;

  public List<Identification> getOtherIdentifications() {
    if (otherIdentifications == null) {
      otherIdentifications = new ArrayList<>();
    }
    return otherIdentifications;
  }
}
