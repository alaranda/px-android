package com.mercadolibre.dto;

import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.dto.public_key.PublicKeyInfo;

public class PublicKeyAndPreference {

    private final PublicKeyInfo publicKey;
    private final Preference preference;

    public PublicKeyAndPreference(final PublicKeyInfo publicKey, final Preference preference) {
        this.publicKey = publicKey;
        this.preference = preference;
    }

    public PublicKeyInfo getPublicKey() {
        return publicKey;
    }

    public Preference getPreference() {
        return preference;
    }
}
