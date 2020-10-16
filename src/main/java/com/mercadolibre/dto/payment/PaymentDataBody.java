package com.mercadolibre.dto.payment;

import java.util.List;

public class PaymentDataBody {

  private String prefId;
  private Long merchantOrderId;
  private List<PaymentData> paymentData;

  public String getPrefId() {
    return prefId;
  }

  public List<PaymentData> getPaymentData() {
    return paymentData;
  }

  public Long getMerchantOrderId() {
    return merchantOrderId;
  }
}
