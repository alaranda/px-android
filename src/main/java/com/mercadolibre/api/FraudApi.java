package com.mercadolibre.api;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.fraud.ResetStatus;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.config.Config;
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
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.CLIENT_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;
import static org.eclipse.jetty.http.HttpStatus.isSuccess;

public class FraudApi {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String PATH = "/fraud/esc_cap_validator";
    private static final String POOL_NAME = "FraudRead";

    static {
        RestUtils.registerPool(POOL_NAME,
                pool ->
                    pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("fraud.socket.timeout"))
        );
    }

    public ResetStatus resetCapEsc(final Context context, final String cardId, final String clientId) throws ApiException {

        final Headers headers = new Headers().add(CLIENT_ID, clientId);
        final URIBuilder url = getPath(cardId);

        try {
            final Response response = RestUtils.newRestRequestBuilder(POOL_NAME).delete(url.toString());
            METRIC_COLLECTOR.incrementCounter(
                    REQUEST_OUT_COUNTER,
                    DatadogUtils.getRequestOutCounterTags(HttpMethod.DELETE.name(), POOL_NAME, response.getStatus())
            );

            if (isSuccess(response.getStatus())) {
                return new ResetStatus();
            }

            throw new ApiException(EXTERNAL_ERROR, "API call to reset cap esc failed", HttpStatus.SC_BAD_GATEWAY);
        } catch (final RestException e) {
            LOGGER.error(
                    LogUtils.getExceptionLog(
                        context.getRequestId(),
                        HttpMethod.DELETE.name(),
                        POOL_NAME, PATH,
                        headers,
                        LogUtils.convertQueryParam(url.getQueryParams()),
                        HttpStatus.SC_GATEWAY_TIMEOUT,
                        e));

            METRIC_COLLECTOR.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
            throw new ApiException(EXTERNAL_ERROR, "API call to reset cap esc failed", HttpStatus.SC_GATEWAY_TIMEOUT);
        }
    }

    static URIBuilder getPath(final String cardId) {
        return new URIBuilder()
                .setScheme(Config.getString("fraud.url.scheme"))
                .setHost(Config.getString("fraud.url.host"))
                .setPath((PATH + "/" + cardId));
    }
}
