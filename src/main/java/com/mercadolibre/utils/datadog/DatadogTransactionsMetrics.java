package com.mercadolibre.utils.datadog;

import static com.mercadolibre.constants.DatadogMetricsNames.COUPONS_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.PAYMENTS_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.PAYMENT_ORDER_TYPE;
import static com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils.*;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.metrics.MetricCollector;
import com.mercadolibre.px.dto.lib.context.UserAgent;

public final class DatadogTransactionsMetrics {

  DatadogTransactionsMetrics() {};

  /**
   * Trackea en datadog todos los datos de la transaccion
   *
   * @param payment payment
   * @param flow flow
   */
  public static void addLegacyPaymentsTransactionData(
      final Payment payment, final String flow, final UserAgent userAgent) {

    MetricCollector.Tags tags = getBasicTransactionMetricTags(payment, flow, userAgent);
    tags.add("collector_id", payment.getCollector().getId());
    METRIC_COLLECTOR.incrementCounter(PAYMENTS_COUNTER, tags);
  }

  public static void addPaymentsTransactionData(
      final Payment payment,
      final String authenticationType,
      final String flow,
      final UserAgent userAgent) {

    MetricCollector.Tags tags = getBasicTransactionMetricTags(payment, flow, userAgent);
    if (Constants.FLOW_NAME_PAYMENTS_BLACKLABEL.equals(authenticationType)) {
      tags.add("client_id", payment.getClientId());
    }
    tags.add("authentication_type", authenticationType);

    METRIC_COLLECTOR.incrementCounter(PAYMENTS_COUNTER, tags);
  }

  private static MetricCollector.Tags getBasicTransactionMetricTags(
      final Payment payment, final String flow, final UserAgent userAgent) {

    addDiscountMetrics(payment);
    return new MetricCollector.Tags()
        .add("site_id", payment.getSiteId())
        .add("status", payment.getStatus())
        .add("status_detail", payment.getStatusDetail())
        .add("payment_method_id", payment.getPaymentMethodId())
        .add("payment_type_id", payment.getPaymentTypeId())
        .add("flow", flow)
        .add("operation_type", payment.getOperationType())
        .add("marketplace", payment.getMarketplace())
        .add("product_id", payment.getProductId())
        .add("os", userAgent.getOperatingSystem().getName());
  }

  private static void addDiscountMetrics(final Payment payment) {
    if (payment.getCouponId() != null) {
      METRIC_COLLECTOR.incrementCounter(COUPONS_COUNTER);
    }
  }

  public static void addOrderTypePayment(final String orderType) {
    METRIC_COLLECTOR.incrementCounter(PAYMENT_ORDER_TYPE, orderType);
  }
}
