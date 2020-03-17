package com.mercadolibre.api;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.risk.RiskResponse;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.config.Config;
import com.mercadolibre.px.toolkit.dao.AbstractDao;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.RestUtils;
import com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogUtils;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.http.HttpMethod;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.mercadolibre.constants.DatadogMetricsNames.POOL_ERROR_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REQUEST_OUT_COUNTER;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;

public class RiskApi extends AbstractDao {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String URL = "/risk_analysis";
    private static final String POOL_NAME = "RiskRead";

    public RiskResponse getRisk(final Context context, final long riskExcecutionId) throws ApiException {

        final Headers headers = new Headers().add(REQUEST_ID, context.getRequestId());
        final URIBuilder url = getPath(riskExcecutionId);

        try {
            final Response response = RestUtils.newRestRequestBuilder(POOL_NAME).get(url.toString());
            METRIC_COLLECTOR.incrementCounter(
                    REQUEST_OUT_COUNTER,
                    DatadogUtils.getRequestOutCounterTags(HttpMethod.GET.name(), POOL_NAME, response.getStatus())
            );

            if (RestUtils.isResponseSuccessful(response)) {
                return RestUtils.responseToObject(response, RiskResponse.class);
            }

            throw new ApiException("external_error", "API call to risk failed", HttpStatus.SC_BAD_GATEWAY);
        } catch (final RestException e) {
            LOGGER.error(
                    LogUtils.getExceptionLog(
                            context.getRequestId(),
                            HttpMethod.GET.name(),
                            POOL_NAME, URL,
                            headers,
                            LogUtils.convertQueryParam(url.getQueryParams()),
                            HttpStatus.SC_GATEWAY_TIMEOUT,
                            e));
            METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
            throw new ApiException("external_error", "API call to risk failed", HttpStatus.SC_GATEWAY_TIMEOUT);
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
    protected void warmUpPool() {
        RestUtils.registerPool(
                getPoolName(),
                pool -> pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("risk.socket.timeout"))
        );
    }
}
