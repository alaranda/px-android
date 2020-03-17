package com.mercadolibre.dto.payment;

import com.mercadolibre.px.dto.lib.card.Issuer;
import com.mercadolibre.px.dto.lib.user.Payer;

import java.math.BigDecimal;

public class PaymentData {

    private BigDecimal rawAmount;
    private BigDecimal transactionAmount;
    private PaymentMethod paymentMethod;
    private Payer payer;
    private Issuer issuer;
    private PayerCost payerCost;
    private Token token;
    private Discount discount;
    private Campaign campaign;

    public BigDecimal getRawAmount() {
        return rawAmount;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Payer getPayer() {
        return payer;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public PayerCost getPayerCost() {
        return payerCost;
    }

    public Token getToken() {
        return token;
    }

    public Discount getDiscount() {
        return discount;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public boolean hasPayerCost(){
        return this.payerCost != null ? true : false;
    }

    public boolean hasIssuer(){
        return this.issuer != null ? true : false;
    }

    public boolean hasDiscount(){ return this.discount != null ? true : false; }

    public boolean hasToken(){
        return this.token != null ? true : false;
    }

    public boolean hasCampaign(){ return this.campaign != null ? true : false; }

    public boolean hasCampaignId() {
        if(this.hasCampaign()){
            return  this.campaign.getId() != null ? true : false;
        }
        return false;
    }

    public boolean hasPayer() { return this.payer != null ? true : false; }
}
