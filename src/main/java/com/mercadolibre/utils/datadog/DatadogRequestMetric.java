package com.mercadolibre.utils.datadog;

import com.mercadolibre.metrics.datadog.DatadogFuryMetricCollector;
import org.apache.http.HttpStatus;
import spark.Request;
import spark.Response;

import java.util.LinkedList;
import java.util.List;

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
        final String[] tags = getMetricTags(request, response).toArray(new String[0]);
        DatadogFuryMetricCollector.INSTANCE.incrementCounter("px.checkout_mobile_payments.request", tags);
    }

    private static List<String> getMetricTags(final Request request, final Response response) {
        final List<String> tags = new LinkedList<>();
        tags.add("request_method:" + request.requestMethod());
        tags.add("request_path:" + request.pathInfo());
        final int statusCode = response.status();
        tags.add("response_status:" + statusCode);
        tags.add("response_status_pattern:" + (statusCode < HttpStatus.SC_OK ? "1XX"
                : statusCode < HttpStatus.SC_MULTIPLE_CHOICES ? "2XX"
                : statusCode < HttpStatus.SC_BAD_REQUEST ? "3XX"
                : statusCode < HttpStatus.SC_INTERNAL_SERVER_ERROR ? "4XX" : "5XX"));
        tags.add("response_content_type:" + response.type());
        return tags;
    }
}