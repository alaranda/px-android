package com.mercadolibre.api;

import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.constants.HeadersConstants.X_REQUEST_ID;
import static com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils.METRIC_COLLECTOR;

import com.mercadolibre.dto.risk.RiskResponse;
import com.mercadolibre.px.api.lib.AbstractDao;
import com.mercadolibre.px.api.lib.dto.ConfigurationDao;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.monitoring.lib.datadog.DatadogUtils;
import com.mercadolibre.px.monitoring.lib.utils.LogUtils;
import com.mercadolibre.px.toolkit.utils.MeliRestUtils;
import com.mercadolibre.px_config.Config;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.retry.SimpleRetryStrategy;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RiskApi extends AbstractDao {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String URL = "/risk_analysis";
  private static final String POOL_NAME = "RiskRead";

  public RiskApi(final ConfigurationDao configurationDao) {
    super(configurationDao);
  }

  public RiskResponse getRisk(final Context context, final long riskExcecutionId)
      throws ApiException {

    final Headers headers = new Headers().add(X_REQUEST_ID, context.getRequestId());
    final URIBuilder url = getPath(riskExcecutionId);

    try {
      final Response response =
          MeliRestUtils.newRestRequestBuilder(POOL_NAME)
              .get(url.toString(), context.getMeliContext());
      METRIC_COLLECTOR.incrementCounter(
          REQUEST_OUT_COUNTER,
          DatadogUtils.getRequestOutCounterTags(
              HttpMethod.GET.name(), POOL_NAME, response.getStatus()));

      if (MeliRestUtils.isResponseSuccessful(response)) {
        return MeliRestUtils.responseToObject(response, RiskResponse.class);
      }

      throw new ApiException(
          "external_error", "API call to risk failed", HttpStatus.SC_BAD_GATEWAY);
    } catch (final RestException e) {
      LOGGER.error(
          LogUtils.getExceptionLog(
              context.getRequestId(),
              HttpMethod.GET.name(),
              POOL_NAME,
              URL,
              headers,
              LogUtils.convertQueryParam(url.getQueryParams()),
              HttpStatus.SC_GATEWAY_TIMEOUT,
              e));
      METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
      throw new ApiException(
          "external_error", "API call to risk failed", HttpStatus.SC_GATEWAY_TIMEOUT);
    }
  }

  static URIBuilder getPath(final long riskExcecutionId) {
    return new URIBuilder()
        .setScheme(Config.getString("risk.url.scheme"))
        .setHost(Config.getString("risk.url.host"))
        .setPath((URL + "/" + riskExcecutionId));
  }

  @Override
  public String getPoolName() {
    return POOL_NAME;
  }

  @Override
  protected void warmUpPool(final ConfigurationDao configurationDao) {
    MeliRestUtils.registerPool(
        getPoolName(),
        pool ->
            pool.withConnectionTimeout(configurationDao.getConnectionTimeout())
                .withSocketTimeout(configurationDao.getSocketTimeout())
                .withRetryStrategy(
                    new SimpleRetryStrategy(
                        configurationDao.getRetry(), configurationDao.getRetryDelay())));
  }
}
