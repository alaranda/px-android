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
    public static void addLegacyPaymentsTransactionData(final Payment payment, final String flow) {
        MetricCollector.Tags tags = getBasicTransactionMetricTags(payment, flow);
        tags.add("collector_id", payment.getCollector().getId());

        METRIC_COLLECTOR.incrementCounter("px.checkout_mobile_payments.payment", tags);
    }

    public static void addPaymentsTransactionData(final Payment payment, final String flow) {
        MetricCollector.Tags tags = getBasicTransactionMetricTags(payment, flow);

        METRIC_COLLECTOR.incrementCounter("px.checkout_mobile_payments.payment", tags);
    }

    private static MetricCollector.Tags getBasicTransactionMetricTags(final Payment payment, final String flow) {

        addDiscountMetrics(payment);
        return new MetricCollector.Tags()
                .add("site_id", payment.getSiteId())
                .add("status", payment.getStatus())
                .add("status_detail", payment.getStatusDetail())
                .add("payment_method_id", payment.getPaymentMethodId())
                .add("flow", flow);
    }

    private static void addDiscountMetrics(final Payment payment) {
        if (payment.getCouponId() != null) {
            METRIC_COLLECTOR.incrementCounter("px.checkout_mobile_payments.coupons");
        }
    }
}
