package com.mercadolibre.service.remedy;

import static com.mercadolibre.constants.DatadogMetricsNames.WITHOUT_REMEDY_COUNTER;

import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;

public class WithoutRemedy implements RemedyInterface {

  @Override
  public RemediesResponse applyRemedy(
      final Context context,
      final RemediesRequest remediesRequest,
      final RemediesResponse remediesResponse) {

    DatadogRemediesMetrics.trackRemediesInfo(WITHOUT_REMEDY_COUNTER, context, remediesRequest);

    return remediesResponse;
  }
}
