package com.mercadolibre.utils.datadog;

import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_IN_COUNTER;
import static com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils.METRIC_COLLECTOR;

import com.mercadolibre.metrics.MetricCollector;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.monitoring.lib.utils.LogUtils;
import com.mercadolibre.utils.assemblers.ContextAssembler;
import spark.Request;
import spark.Response;

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

  private static MetricCollector.Tags getMetricTags(
      final Request request, final Response response) {

    String path = request.pathInfo();

    if (request.pathInfo().contains("/esc_cap/") || request.pathInfo().contains("/remedies/")) {
      path = path.substring(0, path.lastIndexOf("/"));
    }

    final Context context = ContextAssembler.toContext(request);

    return new MetricCollector.Tags()
        .add("request_method", request.requestMethod())
        .add("request_path", path)
        .add("response_status", response.status())
        .add("response_status_pattern", LogUtils.getHttpStatusCodePattern(response.status()))
        .add("flow", context.getFlow())
        .add("site_id", context.getSite())
        .add("os", context.getUserAgent().getOperatingSystem().getName());
  }
}
