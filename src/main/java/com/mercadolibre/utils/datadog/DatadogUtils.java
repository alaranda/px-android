package com.mercadolibre.utils.datadog;

import com.mercadolibre.config.Config;
import com.mercadolibre.metrics.DummyMetricCollector;
import com.mercadolibre.metrics.MetricCollector;
import com.mercadolibre.metrics.datadog.DatadogFuryMetricCollector;
import com.mercadolibre.router.ApiContext;
import org.apache.http.HttpStatus;

public enum DatadogUtils {
    ;

    public static final MetricCollector METRIC_COLLECTOR =
        ApiContext.isInFuryScope(Config.getSCOPE()) ? DatadogFuryMetricCollector.INSTANCE : DummyMetricCollector.INSTANCE;

    /* default */ static String getStatusPattern(final int statusCode) {
        if (statusCode < HttpStatus.SC_OK) {
            return "1XX";
        } else if (statusCode < HttpStatus.SC_MULTIPLE_CHOICES) {
            return "2XX";
        } else if (statusCode < HttpStatus.SC_BAD_REQUEST) {
            return "3XX";
        } else if (statusCode < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            return "4XX";
        } else {
            return "5XX";
        }
    }
}
