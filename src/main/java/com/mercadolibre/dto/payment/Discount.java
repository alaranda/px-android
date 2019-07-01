package com.mercadolibre.dto.payment;

import java.math.BigDecimal;

public class Discount {

    private String id;
    private String name;
    private String currencyId;
    private BigDecimal percentOff;
    private BigDecimal amountOff;
    private BigDecimal couponAmount;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public BigDecimal getPercentOff() {
        return percentOff;
    }

    public BigDecimal getAmountOff() {
        return amountOff;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }
}
