package com.mercadolibre.dto.preference;

public final class PreferenceResponse {

    final private String prefId;
    final private String publickKey;

    public PreferenceResponse (final String prefId, final String publickKey) {
        this.prefId = prefId;
        this.publickKey = publickKey;
    }

    public String getPrefId() {
        return prefId;
    }

    public String getPublickKey() {
        return publickKey;
    }
}
