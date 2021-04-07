package com.mercadolibre.dto.payment;

import java.util.List;

// todo ver nombre correcto
public class PaymentMethod {

  private String id;
  private String name;
  private String paymentTypeId;
  private String status;
  private String secureThumbnail;
  private String deferredCapture;
  private Integer accreditationTime;
  private String merchantAccountId;
  private List<String> additionalInfoNeeded;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPaymentTypeId() {
    return paymentTypeId;
  }

  public String getStatus() {
    return status;
  }

  public String getSecureThumbnail() {
    return secureThumbnail;
  }

  public String getDeferredCapture() {
    return deferredCapture;
  }

  public Integer getAccreditationTime() {
    return accreditationTime;
  }

  public String getMerchantAccountId() {
    return merchantAccountId;
  }

  public List<String> getAdditionalInfoNeeded() {
    return additionalInfoNeeded;
  }
}
