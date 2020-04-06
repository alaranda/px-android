package com.mercadolibre.dto.preference;

public final class PreferenceResponse {

  private final String prefId;
  private final String publicKey;
  private final boolean escEnabled;
  private final boolean onetapEnabled;

  public PreferenceResponse(final String prefId, final String publicKey) {
    this.prefId = prefId;
    this.publicKey = publicKey;
    this.escEnabled = true;
    this.onetapEnabled = true;
  }

  public String getPrefId() {
    return prefId;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public boolean isEscEnabled() {
    return escEnabled;
  }

  public boolean isOnetapEnabled() {
    return onetapEnabled;
  }

  public String toLog(final PreferenceResponse preferenceResponse) {
    return new StringBuilder()
        .append(String.format("public_key: %s - ", preferenceResponse.getPublicKey()))
        .append(String.format("pref_id: %s - ", preferenceResponse.getPrefId()))
        .append(String.format("esc_enabled: %s - ", preferenceResponse.isEscEnabled()))
        .append(String.format("onetap_enabled: %s - ", preferenceResponse.isOnetapEnabled()))
        .toString();
  }
}
