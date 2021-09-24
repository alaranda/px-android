package com.mercadolibre.utils.datadog;

import static com.mercadolibre.constants.DatadogMetricsNames.PREFERENCE_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.PREFERENCE_INVALID;
import static com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils.METRIC_COLLECTOR;

import com.mercadolibre.constants.DatadogTagNames;
import com.mercadolibre.dto.preference.PreferenceResponse;
import com.mercadolibre.metrics.MetricCollector;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.preference.Preference;

public class DatadogPreferencesMetric {

  private DatadogPreferencesMetric() {}

  public static void addPreferenceData(
      final Context context, final PreferenceResponse preferenceResponse) {
    METRIC_COLLECTOR.incrementCounter(
        PREFERENCE_COUNTER, getMetricTags(context, preferenceResponse));
  }

  private static MetricCollector.Tags getMetricTags(
      final Context context, final PreferenceResponse preferenceResponse) {
    final MetricCollector.Tags tags = new MetricCollector.Tags();
    tags.add(DatadogTagNames.FLOW, preferenceResponse.getFlowId());
    tags.add("product_id", preferenceResponse.getProductId());
    addTagsFromContextInfo(context, tags);
    return tags;
  }

  public static void addInvalidPreferenceData(
      final Context context, final Preference preference, final String reason) {
    METRIC_COLLECTOR.incrementCounter(
        PREFERENCE_INVALID, getMetricTagsInvalidPreference(context, preference, reason));
  }

  private static MetricCollector.Tags getMetricTagsInvalidPreference(
      final Context context, final Preference preference, final String reason) {
    final MetricCollector.Tags tags = new MetricCollector.Tags();
    tags.add("operation_type", preference.getOperationType());
    tags.add(DatadogTagNames.FLOW, context.getFlow());
    tags.add("reason", reason);
    addTagsFromContextInfo(context, tags);
    return tags;
  }

  private static void addTagsFromContextInfo(Context context, MetricCollector.Tags tags) {
    if (null != context.getPlatform()) {
      tags.add("platform", context.getPlatform().getName());
    }
    if (null != context.getSite()) {
      tags.add("site_id", context.getSite().getSiteId());
    }
    if (null != context.getUserAgent()) {
      tags.add("os", context.getUserAgent().getOperatingSystem());
    }
  }
}
