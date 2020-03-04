package com.mercadolibre.utils.datadog;

import com.mercadolibre.dto.remedies.RemediesRequest;
import com.mercadolibre.metrics.MetricCollector;
import com.mercadolibre.px.dto.lib.context.Context;

import static com.mercadolibre.utils.datadog.DatadogUtils.METRIC_COLLECTOR;

public class DatadogRemediesMetrics {

    public DatadogRemediesMetrics(){}

    /**
     * Trackea en datadog los datos de los remedies
     *
     * @param context  Context
     * @param remediesRequest  Remedies Request
     */
    public static void trackRemediesInfo(final String metricName, final Context context, final RemediesRequest remediesRequest) {
        METRIC_COLLECTOR.incrementCounter(metricName, getMetricTags(context, remediesRequest));
    }

    private static MetricCollector.Tags getMetricTags(final Context context, final RemediesRequest remediesRequest) {

        final MetricCollector.Tags tags = new MetricCollector.Tags();
        tags.add("flow", context.getFlow());
        tags.add("bussunes", context.getPlattform());
        tags.add("plattform", remediesRequest.getUserAgent().getOperatingSystem().getName());
        tags.add("site", remediesRequest.getSiteId());
        tags.add("status_detail", remediesRequest.getStatusDetail());
        tags.add("paymentTypeIdRejection", remediesRequest.getPayerPaymentMethodRejected().getPaymentTypeId());

        return tags;
    }
}
