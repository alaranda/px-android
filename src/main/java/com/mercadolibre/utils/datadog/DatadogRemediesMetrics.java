package com.mercadolibre.utils.datadog;

import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;

import com.mercadolibre.dto.remedy.PaymentMethodSelected;
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

  public static void trackRemedySilverBulletInfo(
      final String metricName,
      final Context context,
      final RemediesRequest remediesRequest,
      final PaymentMethodSelected paymentMethodSelected) {
    final MetricCollector.Tags tags = getMetricTags(context, remediesRequest);
    String paymentTypeSuggested = "none";
    if (null != paymentMethodSelected
        && null != paymentMethodSelected.getAlternativePayerPaymentMethod()) {
      paymentTypeSuggested =
          paymentMethodSelected.getAlternativePayerPaymentMethod().getPaymentMethodId();
    }
    addSuggestedPaymentMethod(tags, paymentTypeSuggested);
    METRIC_COLLECTOR.incrementCounter(metricName, getMetricTags(context, remediesRequest));
  }

  private static MetricCollector.Tags getMetricTags(
      final Context context, final RemediesRequest remediesRequest) {

    final MetricCollector.Tags tags = new MetricCollector.Tags();
    tags.add("flow", context.getFlow());
    tags.add("bussines", context.getPlatform());
    tags.add("platform", remediesRequest.getUserAgent().getOperatingSystem().getName());
    tags.add("site", remediesRequest.getSiteId());
    tags.add("status_detail", remediesRequest.getStatusDetail());
    tags.add(
        "payment_rejected", remediesRequest.getPayerPaymentMethodRejected().getPaymentMethodId());

    return tags;
  }

  private static void addSuggestedPaymentMethod(
      final MetricCollector.Tags tags, final String suggestedPaymentMethod) {
    tags.add("suggested_payment_method", suggestedPaymentMethod);
  }
}
