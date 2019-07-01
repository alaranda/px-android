package com.mercadolibre.dto.payment;

import java.math.BigDecimal;
import java.util.List;

public class PayerCost {

    private Integer installments;
    private static final String CFT = "CFT";
    private static final String TEA = "TEA";
    private List<String> labels;
    private String recommendedMessage;
    private BigDecimal installmentRate;
    private BigDecimal totalAmount;
    private BigDecimal installmentAmount;
    private String paymentMethodOptionId;

    public Integer getInstallments() {
        return installments;
    }

    public static String getCFT() {
        return CFT;
    }

    public static String getTEA() {
        return TEA;
    }

    public List<String> getLabels() {
        return labels;
    }

    public String getRecommendedMessage() {
        return recommendedMessage;
    }

    public BigDecimal getInstallmentRate() {
        return installmentRate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public String getPaymentMethodOptionId() {
        return paymentMethodOptionId;
    }
}
