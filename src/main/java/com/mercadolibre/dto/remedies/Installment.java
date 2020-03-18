package com.mercadolibre.dto.remedies;

import java.math.BigDecimal;
import java.util.List;

public class Installment {

    private int installments;
    private BigDecimal totalAmount;
    private List<String> labels;
    private String recommendedMessage;

    public int getInstallments() {
        return installments;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<String> getLabels() {
        return labels;
    }

    public String getRecommendedMessage() {
        return recommendedMessage;
    }
}
