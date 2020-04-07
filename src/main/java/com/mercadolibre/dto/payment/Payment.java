package com.mercadolibre.dto.payment;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Objeto que representa la respuesta de payments */
public class Payment {

  private Long id;

  private AdditionalInfo additionalInfo;
  private BigDecimal transactionAmountRefunded;
  private String apiVersion;
  private boolean capture;
  private String clientId;
  private Collector collector;
  private BigDecimal conceptAmount;
  private String conceptId;
  private BigDecimal couponAmount;
  private Long couponId;
  private String currencyId;
  private OffsetDateTime dateApproved;
  private OffsetDateTime dateCreated;
  private Long differentialPricingId;
  private String externalReference;
  private BigDecimal installmentAmount;
  private OffsetDateTime lastModified;
  private String marketplace;
  private BigDecimal marketplaceFee;
  private Long merchantOrderId;
  private String operationType;
  private Long orderId;
  private Long payerId;
  private String paymentMethodId;
  private String paymentMethodReferenceId;
  private String paymentType;
  private String reason;
  private String siteId;
  private String status;
  private String statusDetail;
  private BigDecimal totalPaidAmount;
  private BigDecimal transactionAmount;
  private String paymentTypeId;
  private String productId;
  private long riskExecutionId;

  Payment() {}

  public Long getId() {
    return id;
  }

  public AdditionalInfo getAdditionalInfo() {
    return additionalInfo;
  }

  public BigDecimal getTransactionAmountRefunded() {
    return transactionAmountRefunded;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public boolean isCapture() {
    return capture;
  }

  public String getClientId() {
    return clientId;
  }

  public Collector getCollector() {
    return collector;
  }

  public BigDecimal getConceptAmount() {
    return conceptAmount;
  }

  public String getConceptId() {
    return conceptId;
  }

  public BigDecimal getCouponAmount() {
    return couponAmount;
  }

  public Long getCouponId() {
    return couponId;
  }

  public String getCurrencyId() {
    return currencyId;
  }

  public OffsetDateTime getDateApproved() {
    return dateApproved;
  }

  public OffsetDateTime getDateCreated() {
    return dateCreated;
  }

  public Long getDifferentialPricingId() {
    return differentialPricingId;
  }

  public String getExternalReference() {
    return externalReference;
  }

  public BigDecimal getInstallmentAmount() {
    return installmentAmount;
  }

  public OffsetDateTime getLastModified() {
    return lastModified;
  }

  public String getMarketplace() {
    return marketplace;
  }

  public BigDecimal getMarketplaceFee() {
    return marketplaceFee;
  }

  public Long getMerchantOrderId() {
    return merchantOrderId;
  }

  public String getOperationType() {
    return operationType;
  }

  public Long getOrderId() {
    return orderId;
  }

  public Long getPayerId() {
    return payerId;
  }

  public String getPaymentMethodId() {
    return paymentMethodId;
  }

  public String getPaymentMethodReferenceId() {
    return paymentMethodReferenceId;
  }

  public String getPaymentType() {
    return paymentType;
  }

  public String getReason() {
    return reason;
  }

  public String getSiteId() {
    return siteId;
  }

  public String getStatus() {
    return status;
  }

  public String getStatusDetail() {
    return statusDetail;
  }

  public BigDecimal getTotalPaidAmount() {
    return totalPaidAmount;
  }

  public BigDecimal getTransactionAmount() {
    return transactionAmount;
  }

  public String getPaymentTypeId() {
    return paymentTypeId;
  }

  public String getProductId() {
    return productId;
  }

  public long getRiskExecutionId() {
    return riskExecutionId;
  }

  public static final class Collector {
    private Long id;

    Collector() {}

    public Long getId() {
      return id;
    }
  }

  public String toLog(final Payment payment) {
    return new StringBuilder()
        .append(String.format("id: %s - ", payment.getId()))
        .append(String.format("coupon_amount: %s - ", payment.getCouponAmount()))
        .append(String.format("site_id: %s - ", payment.getSiteId()))
        .append(String.format("payment_method_id: %s - ", payment.getPaymentMethodId()))
        .append(String.format("transaction_amount: %s -", payment.getTransactionAmount()))
        .toString();
  }
}
