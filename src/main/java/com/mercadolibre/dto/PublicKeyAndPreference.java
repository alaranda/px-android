package com.mercadolibre.dto;

import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.dto.lib.user.PublicKey;

public class PublicKeyAndPreference {

  private final PublicKey publicKey;
  private final Preference preference;

  public PublicKeyAndPreference(final PublicKey publicKey, final Preference preference) {
    this.publicKey = publicKey;
    this.preference = preference;
  }

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public Preference getPreference() {
    return preference;
  }
}
