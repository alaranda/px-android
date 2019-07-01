package com.mercadolibre.dto.preference;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class Preference {

    protected String id;
    protected List<PreferenceItem> items;
    protected PreferencePayer payer;
    protected PreferencePaymentMethod paymentMethods;
    protected PreferenceShipment shipments;
    protected BackUrls backUrls;
    private String notificationUrl;
    protected String initPoint;
    protected String sandboxInitPoint;

    protected OffsetDateTime dateCreated;
    protected String operationType;
    protected String additionalInfo;
    protected String autoReturn;
    private String externalReference;
    protected boolean expires;

    protected OffsetDateTime expirationDateFrom;
    protected OffsetDateTime expirationDateTo;

    private Long collectorId;
    protected Long clientId;
    protected String marketplace;
    private BigDecimal marketplaceFee;
    protected DifferentialPricing differentialPricing;
    protected BigDecimal totalAmount;
    private boolean binaryMode;

    Preference() {
    }

    public String getId() {
        return id;
    }

    public List<PreferenceItem> getItems() {
        return items;
    }

    public PreferencePayer getPayer() {
        return payer;
    }

    public PreferencePaymentMethod getPaymentMethods() {
        return paymentMethods;
    }

    public PreferenceShipment getShipments() {
        return shipments;
    }

    public BackUrls getBackUrls() {
        return backUrls;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public String getInitPoint() {
        return initPoint;
    }

    public String getSandboxInitPoint() {
        return sandboxInitPoint;
    }

    public OffsetDateTime getDateCreated() {
        return dateCreated;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public String getAutoReturn() {
        return autoReturn;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public boolean isExpires() {
        return expires;
    }

    public OffsetDateTime getExpirationDateFrom() {
        return expirationDateFrom;
    }

    public OffsetDateTime getExpirationDateTo() {
        return expirationDateTo;
    }

    public Long getCollectorId() {
        return collectorId;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getMarketplace() {
        return marketplace;
    }

    public BigDecimal getMarketplaceFee() {
        return marketplaceFee;
    }

    public DifferentialPricing getDifferentialPricing() {
        return differentialPricing;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public boolean isBinaryMode() {
        return binaryMode;
    }
}
