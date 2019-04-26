package com.mercadolibre.dto.access_token;

import com.google.gson.annotations.SerializedName;

public final class AccessToken {

    private String userId;
    private String status;
    private Long clientId;
    private String siteId;
    @SerializedName("is_test")
    private boolean test;

    private AccessToken() {
    }

    public String getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getSiteId() {
        return siteId;
    }

    public boolean isTest() {
        return test;
    }
}
