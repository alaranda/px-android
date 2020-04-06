package com.mercadolibre.constants;

import static com.mercadolibre.constants.ErrorMessagesConstants.CAN_NOT_INSTANTIATE_CONSTANTS_CLASS;

public final class PaymentsRequestBodyParams {

  public static final String PREF_ID = "pref_id";
  public static final String INSTALLMENTS = "installments";
  public static final String ISSUER_ID = "issuer_id";
  public static final String EMAIL = "email";

  private PaymentsRequestBodyParams() {
    throw new AssertionError(CAN_NOT_INSTANTIATE_CONSTANTS_CLASS);
  }
}
