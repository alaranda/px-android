package com.mercadolibre.utils.datadog;

import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.metrics.MetricCollector;
import static com.mercadolibre.utils.datadog.DatadogUtils.METRIC_COLLECTOR;

public final class DatadogTransactionsMetrics {

    DatadogTransactionsMetrics(){};

    /**
     * Trackea en datadog todos los datos de la transaccion
     *
     * @param payment payment
     * @param flow flow
     */
    public static void addTransactionData(final Payment payment, final String flow) {
        METRIC_COLLECTOR.incrementCounter("px.checkout_mobile_payments.payment", getMetricTags(payment, flow));

        if (payment.getCouponId() != null) {
            METRIC_COLLECTOR.gauge("px.checkout_mobile_payments.payment.coupon_quantity", 1);
            METRIC_COLLECTOR.gauge("px.checkout_mobile_payments.payment.coupon_amount", payment.getCouponAmount().doubleValue());
        }
    }

    private static MetricCollector.Tags getMetricTags(final Payment payment, final String flow) {
        return new MetricCollector.Tags()
                .add("site_id", payment.getSiteId())
                .add("status", payment.getStatus())
                .add("status_detail", payment.getStatusDetail())
                .add("payment_method_id", payment.getPaymentMethodId())
                .add("collector_id", payment.getCollector().getId())
                .add("client_id", payment.getClientId())
                .add("flow", flow);
    }
}
