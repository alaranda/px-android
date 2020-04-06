package com.mercadolibre.dto.congrats;

import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import lombok.ToString;

@ToString
public class CongratsRequest {

    private String userId;
    private String clientId;
    private String siteId;
    private String paymentIds;
    private String platform;
    private UserAgent userAgent;
    private String density;
    private String productId;
    private String campaignId;
    private String flowName;
    private boolean ifpe;
    private String paymentMehotdsIds;

    public CongratsRequest(final String userId, final String clientId, final String siteId, final String paymentIds,
                           final String platform, final UserAgent userAgent, final String density, final String productId,
                           final String campaignId, final String flowName, final boolean ifpe, final String paymentMehotdsIds ) {
        this.userId = userId;
        this.clientId = clientId;
        this.siteId = siteId;
        this.paymentIds = paymentIds;
        this.platform = platform;
        this.userAgent = userAgent;
        this.density = density;
        this.productId = productId;
        this.campaignId = campaignId;
        this.flowName = flowName;
        this.ifpe = ifpe;
        this.paymentMehotdsIds = paymentMehotdsIds;
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

    public String getDensity() {
        return density;
    }

    public String getProductId() { return productId; }

    public String getCampaignId() { return campaignId; }

    public String getFlowName() { return flowName; }

    public boolean isIfpe() { return ifpe; }

    public String getPaymentMehotdsIds() {
        return paymentMehotdsIds;
    }

}
