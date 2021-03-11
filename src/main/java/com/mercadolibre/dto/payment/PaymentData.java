package com.mercadolibre.dto.payment;

import com.mercadolibre.px.dto.lib.card.Issuer;
import com.mercadolibre.px.dto.lib.user.Payer;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class PaymentData {

  private BigDecimal rawAmount;
  private BigDecimal transactionAmount;
  private PaymentMethod paymentMethod;
  private Payer payer;
  private Issuer issuer;
  private PayerCost payerCost;
  private Token token;
  private Discount discount;
  private Campaign campaign;

  public boolean hasPayerCost() {
    return this.payerCost != null;
  }

  public boolean hasIssuer() {
    return this.issuer != null;
  }

  public boolean hasDiscount() {
    return this.discount != null;
  }

  public boolean hasToken() {
    return this.token != null;
  }

  public boolean hasCampaign() {
    return this.campaign != null;
  }

  public boolean hasDiscountToken() {
    if (this.hasDiscount()) {
      return this.discount.getId() != null;
    }
    return false;
  }

  public boolean hasPayer() {
    return this.payer != null;
  }
}
