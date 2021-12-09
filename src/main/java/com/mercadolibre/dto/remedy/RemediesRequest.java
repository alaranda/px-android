package com.mercadolibre.dto.remedy;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RemediesRequest {

  private PayerPaymentMethodRejected payerPaymentMethodRejected;
  private List<AlternativePayerPaymentMethod> alternativePayerPaymentMethods;
  private CustomStringConfiguration customStringConfiguration;

  @Schema(hidden = true)
  @Setter
  private long riskExcecutionId;

  @Schema(hidden = true)
  @Setter
  private String statusDetail;

  @Schema(hidden = true)
  @Setter
  private String siteId;

  @Schema(hidden = true)
  @Setter
  private String userId;

  @Schema(hidden = true)
  @Setter
  private boolean oneTap;

  public void setPaymentMethodRejectedTotalAmount(final BigDecimal totalAmount) {
    this.payerPaymentMethodRejected.setTotalAmount(totalAmount);
  }
}
