package com.mercadolibre.dto.payment;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDataBody {
  private String prefId;
  private Long merchantOrderId;
  private List<PaymentData> paymentData;
  private String validationProgramId;
}
