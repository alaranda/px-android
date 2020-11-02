package com.mercadolibre.dto.remedy;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RemediesRequest {

  private PayerPaymentMethodRejected payerPaymentMethodRejected;
  private List<AlternativePayerPaymentMethod> alternativePayerPaymentMethods;

  @Setter private long riskExcecutionId;
  @Setter private String statusDetail;
  @Setter private String siteId;
  @Setter private String userId;
  @Setter private boolean oneTap;
}
