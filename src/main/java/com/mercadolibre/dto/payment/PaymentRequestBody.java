package com.mercadolibre.dto.payment;


import com.mercadolibre.dto.Payer;

import java.math.BigDecimal;

/**
 * Objeto que mapea el body que recibe el endpoint /px_mobile_api/payments
 */
public class PaymentRequestBody {

    private String prefId;
    private String publicKey;

    private Payer payer;
    private String email;
    private String paymentMethodId;
    private Long collectorId;

    // card fields
    private String issuerId;
    private String token;


    private Integer installments;

    // coupon fields
    private BigDecimal couponAmount;
    private Long campaignId;
    private String couponCode;

    private boolean binaryMode;


    PaymentRequestBody(){}

    public String getPrefId() {
        return prefId;
    }

    public Integer getInstallments() {
        return installments;
    }

    public String getToken() {
        return token;
    }

    public Payer getPayer() {
        return payer;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public String getEmail() {
        return email;
    }

    public Long getCollectorId() {
        return collectorId;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public boolean isBinaryMode() {
        return binaryMode;
    }

    public String getPublicKey() { return publicKey; }

}
