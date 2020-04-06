package com.mercadolibre.utils.datadog;

import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;

import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.metrics.MetricCollector;
import com.mercadolibre.px.dto.lib.context.Context;

public class DatadogRemediesMetrics {

  /**
   * Trackea en datadog los datos de los remedy
   *
   * @param context Context
   * @param remediesRequest Remedies Request
   */
  public static void trackRemediesInfo(
      final String metricName, final Context context, final RemediesRequest remediesRequest) {
    METRIC_COLLECTOR.incrementCounter(metricName, getMetricTags(context, remediesRequest));
  }

  private static MetricCollector.Tags getMetricTags(
      final Context context, final RemediesRequest remediesRequest) {

    final MetricCollector.Tags tags = new MetricCollector.Tags();
    tags.add("flow", context.getFlow());
    tags.add("bussines", context.getPlatform());
    tags.add("plattform", remediesRequest.getUserAgent().getOperatingSystem().getName());
    tags.add("site", remediesRequest.getSiteId());
    tags.add("status_detail", remediesRequest.getStatusDetail());
    tags.add(
        "paymentTypeIdRejection",
        remediesRequest.getPayerPaymentMethodRejected().getPaymentTypeId());

    return tags;
  }
}
