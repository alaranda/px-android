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
}
