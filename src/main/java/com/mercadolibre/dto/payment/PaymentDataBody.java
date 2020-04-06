package com.mercadolibre.dto.payment;

import java.util.List;

public class PaymentDataBody {

  private String prefId;
  private List<PaymentData> paymentData;

  public String getPrefId() {
    return prefId;
  }

  public List<PaymentData> getPaymentData() {
    return paymentData;
  }
}
