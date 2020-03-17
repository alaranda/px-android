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
    public static final String PREFERENCE_IVALID = "px.checkout_mobile_payments.invalid.preference";
    public static final String PAYMENT_ORDER_TYPE = "px.checkout_mobile_payments.order_type";
    public static final String CONGRATS_DISCOUNTS = "px.checkout_mobile_payments.congrats_discounts";
    public static final String CONGRATS_POINTS = "px.checkout_mobile_payments.congrats_points";
    public static final String CONGRATS_CROSS_SELLING = "px.checkout_mobile_payments.congrats_cross_selling";
    public static final String CONGRATS_ERROR_BUILD_CONGRATS = "px.RemediesRequest.congrats_error_build_congrats";
    public static final String REMEDIES_COUNTER = "px.checkout_mobile_payments.remedies.counter";
    public static final String REMEDIES_ALTERNATIVE_PAYMENT_METHOD = "px.checkout_mobile_payments.remedies.alternative_payment_method";
    public static final String REMEDIES_ALTERNATIVE_PAYMENT_METHOD_ESC = "px.checkout_mobile_payments.remedies.alternative_payment_method.esc";
    public static final String REMEDIES_KYC_FAIL_COUNTER = "px.checkout_mobile_payments.remedies.counter";


}
