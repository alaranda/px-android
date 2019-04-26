package com.mercadolibre.utils.datadog;

import com.mercadolibre.metrics.datadog.DatadogFuryMetricCollector;
import com.mercadolibre.restclient.Response;
import org.apache.http.HttpStatus;

import java.util.LinkedList;
import java.util.List;

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
        final String[] tags = getMetricTags(method, poolName, response).toArray(new String[0]);
        DatadogFuryMetricCollector.INSTANCE.incrementCounter("px.checkout_mobile_payments.request_out", tags);
    }

    private static List<String> getMetricTags(final String method, final String poolName, final Response response) {
        final List<String> tags = new LinkedList<>();
        tags.add("request_method:" + method);
        tags.add("request_source:" + poolName);
        final int statusCode = response.getStatus();
        tags.add("response_status:" + statusCode);
        tags.add("response_status_pattern:" + (statusCode < HttpStatus.SC_OK ? "1XX"
                : statusCode < HttpStatus.SC_MULTIPLE_CHOICES ? "2XX"
                : statusCode < HttpStatus.SC_BAD_REQUEST ? "3XX"
                : statusCode < HttpStatus.SC_INTERNAL_SERVER_ERROR ? "4XX" : "5XX"));
        return tags;
    }
}
