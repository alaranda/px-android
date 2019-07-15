package com.mercadolibre.utils.datadog;

import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.metrics.MetricCollector;
import com.sun.jndi.toolkit.url.Uri;
import java.net.MalformedURLException;
import static com.mercadolibre.utils.datadog.DatadogUtils.METRIC_COLLECTOR;

public class DatadogPreferencesMetric {

    private DatadogPreferencesMetric() {
    }

    /**
     * Trackea en datadog los datos de la preferencia
     *
     * @param preference  Preference
     */

    public static void addPreferenceData(final Preference preference) throws MalformedURLException {
        METRIC_COLLECTOR.incrementCounter("px.checkout_mobile_payments.preference", getMetricTags(preference));
    }

    private static MetricCollector.Tags getMetricTags(final Preference preference) throws MalformedURLException {
        return new MetricCollector.Tags()
                .add("client_id", preference.getClientId())
                .add("init_url", formatUrl(preference.getInitPoint()));
    }

    private static String formatUrl(final String url) throws MalformedURLException {
        String initUrl = "";
        if (url != null) {
            final Uri uri = new Uri(url);
            initUrl = uri.getHost() + uri.getPath();
        }
        return initUrl;
    }
}
