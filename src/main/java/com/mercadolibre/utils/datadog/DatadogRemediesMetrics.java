package com.mercadolibre.utils.datadog;

import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;

import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.tracking.TrackingData;
import com.mercadolibre.metrics.MetricCollector;
import com.mercadolibre.px.dto.lib.context.Context;

public class DatadogRemediesMetrics {

  /**
   * Trackea en datadog los datos de los remedy
   *
   * @param metricName String
   * @param context Context
   * @param remediesRequest Remedies Request
   */
  public static void trackRemediesInfo(
      final String metricName, final Context context, final RemediesRequest remediesRequest) {
    METRIC_COLLECTOR.incrementCounter(metricName, getMetricTags(context, remediesRequest));
  }

  public static void trackRemedySilverBulletInfo(
      final String metricName,
      final Context context,
      final RemediesRequest remediesRequest,
      final TrackingData trackingData) {

    final MetricCollector.Tags tags = getMetricTags(context, remediesRequest);

    tags.add("suggested_payment_method", trackingData.getPaymentMethodId());
    tags.add("suggested_payment_type", trackingData.getPaymentTypeId());
    tags.add("suggested_intallments", trackingData.getInstallments());
    tags.add("suggested_amount", trackingData.getAmount());
    tags.add("frictionless", trackingData.getFrictionless());

    METRIC_COLLECTOR.incrementCounter(metricName, tags);
  }

  private static MetricCollector.Tags getMetricTags(
      final Context context, final RemediesRequest remediesRequest) {

    final MetricCollector.Tags tags = new MetricCollector.Tags();
    tags.add("flow", context.getFlow());
    tags.add("bussines", context.getPlatform());
    tags.add("platform", context.getUserAgent().getOperatingSystem().getName());
    tags.add("site", remediesRequest.getSiteId());
    tags.add("status_detail", remediesRequest.getStatusDetail());
    if (remediesRequest.getPayerPaymentMethodRejected() != null) {
      tags.add(
          "payment_rejected", remediesRequest.getPayerPaymentMethodRejected().getPaymentMethodId());
    }
    return tags;
  }
}
