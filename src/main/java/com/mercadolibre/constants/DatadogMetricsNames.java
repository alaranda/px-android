package com.mercadolibre.constants;

import com.mercadolibre.px.toolkit.exceptions.CanNotInstantiateError;

public final class DatadogMetricsNames {
  private DatadogMetricsNames() {
    throw new CanNotInstantiateError();
  }

  public static final String REQUEST_IN_COUNTER = "px.checkout_mobile_payments.request";
  public static final String REQUEST_OUT_COUNTER = "px.checkout_mobile_payments.request_out";
  public static final String POOL_ERROR_COUNTER = "px.checkout_mobile_payments.errors.pool";
  public static final String PAYMENTS_COUNTER = "px.checkout_mobile_payments.payment";
  public static final String COUPONS_COUNTER = "px.checkout_mobile_payments.coupons";
  public static final String PREFERENCE_COUNTER = "px.checkout_mobile_payments.preference";
  public static final String PREFERENCE_INVALID = "px.checkout_mobile_payments.invalid.preference";
  public static final String PAYMENT_ORDER_TYPE = "px.checkout_mobile_payments.order_type";
  public static final String CONGRATS_REQUEST = "px.checkout_mobile_payments.congrats.request";
  public static final String CONGRATS_DISCOUNTS = "px.checkout_mobile_payments.congrats_discounts";
  public static final String CONGRATS_POINTS = "px.checkout_mobile_payments.congrats_points";
  public static final String CONGRATS_CROSS_SELLING =
      "px.checkout_mobile_payments.congrats_cross_selling";
  public static final String CONGRATS_ERROR_BUILD_CONGRATS =
      "px.RemediesRequest.congrats_error_build_congrats";
  // Remedies
  public static final String REMEDY_SILVER_BULLET_COUNTER =
      "px.checkout_mobile_payments.remedy_silver_bullet";
  public static final String REMEDY_HIGH_RISK_COUNTER =
      "px.checkout_mobile_payments.remedy_high_risk";
  public static final String REMEDY_HIGH_RISK_TAGGED_COUNTER =
      "px.checkout_mobile_payments.remedy_high_risk_tagged";
  public static final String REMEDY_CVV_COUNTER = "px.checkout_mobile_payments.remedy_cvv";
  public static final String REMEDY_CALL_FOR_AUTHORIZE_COUNTER =
      "px.checkout_mobile_payments.remedy_call_for_authorize";
  public static final String WITHOUT_REMEDY_COUNTER = "px.checkout_mobile_payments.without_remedy";
  public static final String REMEDY_KYC_FAIL_COUNTER =
      "px.checkout_mobile_payments.remedy.kyc_fail";
  public static final String SILVER_BULLET_WITHOUT_PM_COUNTER =
      "px.checkout_mobile_payments.silver_bullet_without_pm";
}
