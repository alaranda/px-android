package com.mercadolibre.utils.datadog;

import com.mercadolibre.metrics.MetricCollector;
import spark.Request;
import spark.Response;

import static com.mercadolibre.utils.datadog.DatadogUtils.METRIC_COLLECTOR;
import static com.mercadolibre.utils.datadog.DatadogUtils.getStatusPattern;

public final class DatadogRequestMetric {

    private DatadogRequestMetric() {
    }

    /**
     * Trackea en datadog todos los request in
     *
     * @param request request
     * @param response response
     */
    public static void incrementRequestCounter(final Request request, final Response response) {
        METRIC_COLLECTOR.incrementCounter("px.checkout_mobile_payments.request", getMetricTags(request, response));
    }

    private static MetricCollector.Tags getMetricTags(final Request request, final Response response) {
        return new MetricCollector.Tags()
                .add("request_method", request.requestMethod())
                .add("request_path", request.pathInfo())
                .add("response_status", response.status())
                .add("response_status_pattern", getStatusPattern(response.status()))
                .add("response_content_type", response.type());
    }
}