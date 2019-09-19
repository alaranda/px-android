package com.mercadolibre.dto.congrats;

import com.mercadolibre.dto.user_agent.UserAgent;

public final class CongratsRequest {

    private String userId;
    private String clientId;
    private String siteId;
    private String paymentIds;
    private String platform;
    private UserAgent userAgent;

    public CongratsRequest(final String userId, final String clientId, final String siteId, final String paymentIds,
                           final String platform, final UserAgent userAgent) {
        this.userId = userId;
        this.clientId = clientId;
        this.siteId = siteId;
        this.paymentIds = paymentIds;
        this.platform = platform;
        this.userAgent = userAgent;
    }

    public String getUserId() {
        return userId;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getPaymentIds() {
        return paymentIds;
    }

    public String getPlatform() { return platform; }

    public String getClientId() { return clientId; }

    public UserAgent getUserAgent() { return userAgent; }
}
