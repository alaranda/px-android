package com.mercadolibre.dto.preference;

public final class PreferenceResponse {

    final private String prefId;
    final private String publicKey;

    public PreferenceResponse (final String prefId, final String publicKey) {
        this.prefId = prefId;
        this.publicKey = publicKey;
    }

    public String getPrefId() {
        return prefId;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
