package com.mercadolibre.dto.remedies;

import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;

import java.util.List;

public class RemediesRequest {

    private PayerPaymentMethodRejected payerPaymentMethodRejected;
    private List<AlternativePayerPaymentMethod> alternativePayerPaymentMethods;
    private long riskExcecutionId;
    private String statusDetail;

    private UserAgent userAgent;
    private String siteId;
    private String userId;

    public PayerPaymentMethodRejected getPayerPaymentMethodRejected() {
        return payerPaymentMethodRejected;
    }

    public List<AlternativePayerPaymentMethod> getAlternativePayerPaymentMethods() {
        return alternativePayerPaymentMethods;
    }

    public void setRiskExcecutionId(long riskExcecutionId) {
        this.riskExcecutionId = riskExcecutionId;
    }

    public long getRiskExcecutionId() {
        return riskExcecutionId;
    }

    public void setUserAgent(UserAgent userAgent) {
        this.userAgent = userAgent;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
