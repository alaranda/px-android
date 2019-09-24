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
    public static final String CONGRATS_ERROR_BUILD_CONGRATS = "px.checkout_mobile_payments.congrats_error_build_congrats";

}
