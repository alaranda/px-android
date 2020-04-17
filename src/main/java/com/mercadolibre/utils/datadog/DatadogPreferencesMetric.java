package com.mercadolibre.utils.datadog;

import static com.mercadolibre.constants.DatadogMetricsNames.PREFERENCE_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.PREFERENCE_IVALID;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.mercadolibre.metrics.MetricCollector;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.dto.lib.user.PublicKey;

public class DatadogPreferencesMetric {

  private DatadogPreferencesMetric() {}

  /**
   * Trackea en datadog los datos de la preferencia
   *
   * @param preference Preference
   */
  public static void addPreferenceData(
      final Preference preference, final PublicKey publicKey, final String userAgent) {
    METRIC_COLLECTOR.incrementCounter(
        PREFERENCE_COUNTER, getMetricTags(preference, publicKey.getSiteId(), "", "", userAgent));
  }

  public static void addInvalidPreferenceData(final Preference preference, final Context context) {
    METRIC_COLLECTOR.incrementCounter(
        PREFERENCE_IVALID,
        getMetricTags(
            preference,
            context.getSite() == null ? "" : context.getSite().getSiteId(),
            context.getFlow(),
            context.getPlatform() == null ? "" : context.getPlatform().getName(),
            ""));
  }

  private static MetricCollector.Tags getMetricTags(
      final Preference preference,
      final String siteId,
      final String flow,
      final String platform,
      final String userAgent) {
    final MetricCollector.Tags tags = new MetricCollector.Tags();
    tags.add("operation_type", preference.getOperationType());
    tags.add("marketplace", preference.getMarketplace());
    if (!isBlank(platform)) {
      tags.add("platform", platform);
    }
    if (!isBlank(siteId)) {
      tags.add("site_id", siteId);
    }
    if (!isBlank(flow)) {
      tags.add("flow", flow);
    }
    if (!isBlank(userAgent)) {
      tags.add("user_agent", userAgent);
    }

    return tags;
  }
}
