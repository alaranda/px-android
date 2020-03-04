package com.mercadolibre.dto.remedies;

import java.math.BigDecimal;

public class PayerPaymentMethodRejected {

    private String paymentMethodId;
    private String paymentTypeId;
    private String issuerName;
    private String lastFourDigit;
    private String securityCodeLocation;
    private int securityCodeLength;
    private BigDecimal totalAmount;
    private int installments;

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getLastFourDigit() {
        return lastFourDigit;
    }

    public String getSecurityCodeLocation() {
        return securityCodeLocation;
    }

    public int getSecurityCodeLength() {
        return securityCodeLength;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public int getInstallments() {
        return installments;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }
}
