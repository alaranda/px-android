package com.mercadolibre.dto.remedies;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class Installment {

    private int installments;
    private BigDecimal totalAmount;
    private List<String> labels;
    private String recommendedMessage;

}
