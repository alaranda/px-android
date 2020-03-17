package com.mercadolibre.utils.datadog;

import com.mercadolibre.metrics.MetricCollector;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogUtils;
import spark.Request;
import spark.Response;

import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_IN_COUNTER;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;

public final class DatadogRequestMetric {
    /**
     * Trackea en datadog todos los request in
     *
     * @param request request
     * @param response response
     */
    public static void incrementRequestCounter(final Request request, final Response response) {
        METRIC_COLLECTOR.incrementCounter(REQUEST_IN_COUNTER, getMetricTags(request, response));
    }

    private static MetricCollector.Tags getMetricTags(final Request request, final Response response) {
        return new MetricCollector.Tags()
                .add("request_method", request.requestMethod())
                .add("request_path", request.pathInfo())
                .add("response_status", response.status())
                .add("response_status_pattern", LogUtils.getHttpStatusCodePattern(response.status()))
                .add("user_agent", request.userAgent());
    }
}