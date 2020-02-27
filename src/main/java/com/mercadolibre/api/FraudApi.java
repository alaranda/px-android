package com.mercadolibre.api;

import com.mercadolibre.config.Config;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.fraud.ResetStatus;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.utils.DatadogUtils;
import com.mercadolibre.px.toolkit.utils.logs.LogUtils;
import com.mercadolibre.rest.RESTUtils;
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
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.CLIENT_ID;

public class FraudApi {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String URL = "/fraud/esc_cap_validator";
    private static final String POOL_NAME = "FraudRead";

    static {
        RESTUtils.registerPool(POOL_NAME, pool ->
                pool.withConnectionTimeout(Config.getLong(Constants.SERVICE_CONNECTION_TIMEOUT_PROPERTY_KEY))
                        .withSocketTimeout(Config.getLong("fraud.socket.timeout"))
        );
    }

    public ResetStatus resetCapEsc(final Context context, final String cardId, final String clientId) throws ApiException {

        final Headers headers = new Headers().add(CLIENT_ID, clientId);
        final URIBuilder url = getPath(cardId);

        try {
            final Response response = RESTUtils.newRestRequestBuilder(POOL_NAME).delete(url.toString());
            DatadogUtils.metricCollector.incrementCounter(
                    REQUEST_OUT_COUNTER,
                    DatadogUtils.getRequestOutCounterTags(HttpMethod.GET.name(), POOL_NAME, response.getStatus())
            );

            if (RESTUtils.isResponseSuccessful(response)) {
                return new ResetStatus();
            }

            throw new ApiException("external_error", "API call to reset cap esc failed", HttpStatus.SC_BAD_GATEWAY);
        } catch (final RestException e) {
            LOGGER.error(LogUtils.getExceptionLog(context.getRequestId(), HttpMethod.GET.name(), POOL_NAME, URL, headers, LogUtils.convertQueryParam(url.getQueryParams()), e));
            DatadogUtils.metricCollector.incrementCounter(POOL_ERROR_COUNTER, "pool:" + POOL_NAME);
            throw new ApiException("external_error", "API call to reset cap esc failed", HttpStatus.SC_GATEWAY_TIMEOUT);
        }
    }

    static URIBuilder getPath(final String cardId) {
        return new URIBuilder()
                .setScheme(Config.getString("fraud.url.scheme"))
                .setHost(Config.getString("fraud.url.host"))
                .setPath((URL + "/" + cardId));
    }
}
