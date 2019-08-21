package com.mercadolibre.dto.preference;

public final class PreferenceResponse {

    final private String prefId;
    final private String publicKey;
    final private boolean escEnabled;
    final private boolean expressEnabled;

    public PreferenceResponse (final String prefId, final String publicKey) {
        this.prefId = prefId;
        this.publicKey = publicKey;
        this.escEnabled = true;
        this.expressEnabled = false;
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

    public boolean isExpressEnabled() {
        return expressEnabled;
    }

    public String toLog(final PreferenceResponse preferenceResponse){
        return new StringBuilder()
                .append(String.format("public_key: %s - ", preferenceResponse.getPublicKey()))
                .append(String.format("pref_id: %s - ", preferenceResponse.getPrefId()))
                .append(String.format("esc_enabled: %s - ", preferenceResponse.isEscEnabled()))
                .append(String.format("express_enabled: %s - ", preferenceResponse.isExpressEnabled()))
                .toString();
    }
}
