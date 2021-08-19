package com.mercadolibre.dto.payment;

import com.mercadolibre.px.dto.lib.card.v4.Card;
import com.mercadolibre.px.dto.lib.preference.PointOfInteraction;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Getter;

/** Objeto que representa la respuesta de payments */
@Getter
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
  private String reason;
  private String siteId;
  private String status;
  private String statusDetail;
  private BigDecimal totalPaidAmount;
  private BigDecimal transactionAmount;
  private String paymentTypeId;
  private String productId;
  private String processingMode;
  private long riskExecutionId;
  private PointOfInteraction pointOfInteraction;
  private Card card;

  @Getter
  public static final class Collector {
    private Long id;
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
