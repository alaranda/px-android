package com.mercadolibre.utils.datadog;

import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.metrics.datadog.DatadogFuryMetricCollector;

import java.util.LinkedList;
import java.util.List;

public final class DatadogTransactionsMetrics {

    DatadogTransactionsMetrics(){};

    /**
     * Trackea en datadog todos los datos de la transaccion
     *
     * @param payment payment
     * @param flow flow
     */
    public static void addTransactionData(final Payment payment, final String flow) {
        DatadogFuryMetricCollector.INSTANCE.incrementCounter("px.checkout_mobile_payments.payment", getMetricTagsPayments(payment, flow));
        DatadogFuryMetricCollector.INSTANCE.gauge("px.checkout_mobile_payments.payment.transaction_amount", payment.getTransactionAmount().doubleValue());

        if (payment.getCouponId() != null) {
            DatadogFuryMetricCollector.INSTANCE.gauge("px.checkout_mobile_payments.payment.coupon_quantity", 1);
            DatadogFuryMetricCollector.INSTANCE.gauge("px.checkout_mobile_payments.payment.coupon_amount", payment.getCouponAmount().doubleValue());
        }
    }

    private static String[] getMetricTagsPayments(final Payment payment, final String flow) {
        final List<String> tags = new LinkedList<>();
        tags.add("site_id:" + payment.getSiteId());
        tags.add("status:" + payment.getStatus());
        tags.add("status_detail:" + payment.getStatusDetail());
        tags.add("payment_method_id:" + payment.getPaymentMethodId());
        tags.add("marketplace:" + payment.getMarketplace());
        tags.add("collector_id:" + payment.getCollector().getId());
        tags.add("flow:" + flow);

        return tags.toArray(new String[0]);
    }
}
