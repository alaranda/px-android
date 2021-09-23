package com.mercadolibre.dto.remedy;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RemediesRequest {

  private PayerPaymentMethodRejected payerPaymentMethodRejected;
  private List<AlternativePayerPaymentMethod> alternativePayerPaymentMethods;
  private CustomStringConfiguration customStringConfiguration;

  @Setter private long riskExcecutionId;
  @Setter private String statusDetail;
  @Setter private String siteId;
  @Setter private String userId;
  @Setter private boolean oneTap;

  public void setPaymentMethodRejectedTotalAmount(final BigDecimal totalAmount) {
    this.payerPaymentMethodRejected.setTotalAmount(totalAmount);
  }
}
