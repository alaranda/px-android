package com.mercadolibre.utils.datadog;

import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.metrics.MetricCollector;

import static com.mercadolibre.constants.DatadogMetricsNames.PREFERENCE_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.PREFERENCE_IVALID;
import static com.mercadolibre.utils.datadog.DatadogUtils.METRIC_COLLECTOR;

public class DatadogPreferencesMetric {

    private DatadogPreferencesMetric() {
    }

    /**
     * Trackea en datadog los datos de la preferencia
     *
     * @param preference  Preference
     */

    public static void addPreferenceData(final Preference preference) {
        METRIC_COLLECTOR.incrementCounter(PREFERENCE_COUNTER, getMetricTags(preference));
    }

    private static MetricCollector.Tags getMetricTags(final Preference preference) {
        return new MetricCollector.Tags()
                .add("operation_type", preference.getOperationType());
    }

    public static void addInvalidPreferenceData(final Preference preference) {
        METRIC_COLLECTOR.incrementCounter(PREFERENCE_IVALID, getMetricTags(preference));
    }

}
