package com.mercadolibre.dto.remedies;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PayerPaymentMethodRejected {

    private String paymentMethodId;
    private String paymentTypeId;
    private String issuerName;
    private String lastFourDigit;
    private String securityCodeLocation;
    private int securityCodeLength;
    private BigDecimal totalAmount;
    private int installments;

}
