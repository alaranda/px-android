package com.mercadolibre.utils.datadog;

import com.mercadolibre.metrics.MetricCollector;
import com.mercadolibre.restclient.Response;
import static com.mercadolibre.utils.datadog.DatadogUtils.METRIC_COLLECTOR;
import static com.mercadolibre.utils.datadog.DatadogUtils.getStatusPattern;

public final class DatadogRequestOutMetric {

    private DatadogRequestOutMetric() {
    }

    /**
     * Trackea en datadog todos los API calls externos
     *
     * @param method   metodo (POST, GET, PUT)
     * @param poolName APIs pool name
     * @param response response de api call externos
     */

    public static void incrementRequestOutCounter(final String method, final String poolName, final Response response) {
        METRIC_COLLECTOR.incrementCounter("px.checkout_mobile_payments.request_out", getMetricTags(method, poolName, response));
    }

    private static MetricCollector.Tags getMetricTags(final String method, final String poolName, final Response response) {
        return new MetricCollector.Tags()
                .add("request_method", method)
                .add("request_source", poolName)
                .add("response_status", response.getStatus())
                .add("response_status_pattern", getStatusPattern(response.getStatus()));
    }
}
