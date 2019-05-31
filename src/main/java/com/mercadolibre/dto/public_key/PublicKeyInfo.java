package com.mercadolibre.dto.public_key;

import java.time.OffsetDateTime;

public class PublicKeyInfo {

    private String publicKey;
    private String testPublicKey;
    private long clientId;
    private long ownerId;
    private String siteId;
    private OffsetDateTime dateCreated;
    private OffsetDateTime dateLastUpdated;

    public PublicKeyInfo() { }

    public PublicKeyInfo(final String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getTestPublicKey() {
        return testPublicKey;
    }

    public long getClientId() {
        return clientId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public String getSiteId() {
        return siteId;
    }

    public OffsetDateTime getDateCreated() {
        return dateCreated;
    }

    public OffsetDateTime getDateLastUpdated() {
        return dateLastUpdated;
    }
}
