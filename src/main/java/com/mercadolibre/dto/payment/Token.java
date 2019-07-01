package com.mercadolibre.dto.payment;

import java.util.Date;

public class Token {

    private String id;
    private String publicKey;
    private String cardId;
    private String luhnValidation;
    private String status;
    private String usedDate;
    private Integer cardNumberLength;
    private Date creationDate;
    private String truncCardNumber;
    private Integer securityCodeLength;
    private Integer expirationMonth;
    private Integer expirationYear;
    private Date lastModifiedDate;
    private Date dueDate;
    private String firstSixDigits;
    private String lastFourDigits;
    private String esc;

    public String getId() {
        return id;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getCardId() {
        return cardId;
    }

    public String getLuhnValidation() {
        return luhnValidation;
    }

    public String getStatus() {
        return status;
    }

    public String getUsedDate() {
        return usedDate;
    }

    public Integer getCardNumberLength() {
        return cardNumberLength;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getTruncCardNumber() {
        return truncCardNumber;
    }

    public Integer getSecurityCodeLength() {
        return securityCodeLength;
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getFirstSixDigits() {
        return firstSixDigits;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public String getEsc() {
        return esc;
    }
}
