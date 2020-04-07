package com.mercadolibre.utils.datadog;

import static com.mercadolibre.constants.DatadogMetricsNames.CONGRATS_CROSS_SELLING;
import static com.mercadolibre.constants.DatadogMetricsNames.CONGRATS_DISCOUNTS;
import static com.mercadolibre.constants.DatadogMetricsNames.CONGRATS_POINTS;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;

import com.mercadolibre.dto.congrats.Congrats;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.metrics.MetricCollector;

public class DatadogCongratsMetric {

  private DatadogCongratsMetric() {}

  /**
   * Trackea en datadog los datos de las congrats
   *
   * @param congrats Congrats
   * @param congratsRequest CongratsRequest
   */
  public static void trackCongratsData(
      final Congrats congrats, final CongratsRequest congratsRequest) {

    if (congrats.hasPoints()) {
      METRIC_COLLECTOR.incrementCounter(CONGRATS_POINTS, getMetricTags(congratsRequest, 0));
    }

    if (congrats.hasDiscounts()) {
      final MetricCollector.Tags tags =
          getMetricTags(congratsRequest, congrats.getDiscounts().discountsSize());
      METRIC_COLLECTOR.incrementCounter(CONGRATS_DISCOUNTS, tags);
    }

    if (null != congrats.getCrossSelling()) {
      METRIC_COLLECTOR.incrementCounter(
          CONGRATS_CROSS_SELLING,
          getMetricTags(congratsRequest, congrats.getCrossSelling().size()));
    }
  }

  private static MetricCollector.Tags getMetricTags(
      final CongratsRequest congratsRequest, final int quantity) {

    final MetricCollector.Tags tags = new MetricCollector.Tags();
    tags.add("site", congratsRequest.getSiteId());
    tags.add("platform", congratsRequest.getPlatform());
    tags.add("productId", congratsRequest.getProductId());

    if (quantity > 0) {
      tags.add("quantity", quantity);
    }

    return tags;
  }
}
