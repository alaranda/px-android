package com.mercadolibre.dto.tracking;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrackingData {

  private String paymentTypeId;
  private String paymentMethodId;
  private String amount;
  private String installments;
  private String frictionless;
}
