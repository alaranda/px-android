package com.mercadolibre.dto.payment;

import java.math.BigDecimal;
import java.util.Date;

public class Campaign {

    private String id;
    private String codeType;
    private BigDecimal maxCouponAmount;
    private int maxRedeemPerUser;
    private Date endDate;
    private String legalTerms;

    public String getId() {
        return id;
    }

    public String getCodeType() {
        return codeType;
    }

    public BigDecimal getMaxCouponAmount() {
        return maxCouponAmount;
    }

    public int getMaxRedeemPerUser() {
        return maxRedeemPerUser;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getLegalTerms() {
        return legalTerms;
    }
}
