package com.mercadolibre.utils.logs;

import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.utils.datadog.DatadogRequestOutMetric;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;
import spark.Request;
import spark.utils.StringUtils;

import java.util.List;
import java.util.Optional;

public final class MonitoringUtils {

    private static final Logger LOG = Logger.getLogger(MonitoringUtils.class);

    /**
     * Log rest client response
     *
     * @param method      http method
     * @param poolName    pool name
     * @param url         url
     * @param queryParams query params
     * @param response    rest client response
     * @param headers     headers
     */
    public static void logResponse(final String method, final String poolName, final String url, final List<NameValuePair> queryParams,
                                   final Response response, final Headers headers) {

        final String params = convertQueryParam(queryParams);
        logResponse(method, poolName, url, params, response, headers, true);

    }

    /**
     * Log rest client response
     *
     * @param method   http method
     * @param poolName pool name
     * @param url      url
     * @param params   params
     * @param response rest client response
     * @param headers  headers
     */
    public static void logResponse(final String method, final String poolName, final String url, final String params,
                                   final Response response, final Headers headers) {

        logResponse(method, poolName, url, params, response, headers, true);
    }

    /**
     * Log rest client response without response body
     *
     * @param method      http method
     * @param poolName    pool name
     * @param url         url
     * @param queryParams query params
     * @param response    rest client response
     * @param headers     headers
     */
    public static void logWithoutResponseBody(final String method, final String poolName, final String url, final List<NameValuePair> queryParams,
                                              final Response response, final Headers headers) {

        final String params = convertQueryParam(queryParams);
        logResponse(method, poolName, url, params, response, headers, false);

    }

    /**
     * Log rest client response without response body
     *
     * @param method   http method
     * @param poolName pool name
     * @param url      url
     * @param response rest client response
     * @param headers  headers
     */
    public static void logWithoutResponseBody(final String method, final String poolName, final String url,
                                              final Response response, final Headers headers) {
        logResponse(method, poolName, url, null, response, headers, false);
    }

    /**
     * Log rest client response and increment datadog request out counter
     *
     * @param method   http method
     * @param poolName pool name
     * @param url      url
     * @param params   params
     * @param response rest client response
     * @param headers  headers
     * @param logBody  (boolean)
     */
    private static void logResponse(final String method, final String poolName, final String url, final String params,
                                    final Response response, final Headers headers, final boolean logBody) {
        final String level = response.getStatus() < HttpStatus.SC_BAD_REQUEST ? LogBuilder.LEVEL_INFO : LogBuilder.LEVEL_ERROR;

        LogBuilder logBuilder = new LogBuilder(level, LogBuilder.REQUEST_OUT)
                .withSource(poolName)
                .withMethod(method)
                .withStatus(response.getStatus())
                .withUrl(url);

        if (headers != null) {
            logBuilder.withHeaders(headers.toString());
        }

        if (!StringUtils.isBlank(params)) {
            logBuilder.withParams(params);
        }

        try {
            if (!StringUtils.isBlank(response.getString()) && (logBody || LogBuilder.LEVEL_ERROR.equals(level))) {
                logBuilder.withResponse(response.getString());
            }
        } catch (final NullPointerException e) {
            logBuilder.withException(e);
        }

        if (response.getStatus() < HttpStatus.SC_BAD_REQUEST) {
            LOG.info(logBuilder.build());
        } else {
            LOG.error(logBuilder.build());
        }
        DatadogRequestOutMetric.incrementRequestOutCounter(method, poolName, response);
    }

    public static void logException(final String method, final String poolName,
                                    final String url,
                                    final Headers headers, Exception e) {
        logException(method, poolName, url, "", headers, e);
    }

    public static void logException(final String method, final String poolName,
                                    final String url, List<NameValuePair> queryParams,
                                    final Headers headers, Exception e) {
        String params = convertQueryParam(queryParams);
        logException(method, poolName, url, params, headers, e);

    }

    public static void logException(final String method, final String poolName,
                                    final String url, final String params,
                                    final Headers headers, final Exception e) {

        final LogBuilder logBuilder = new LogBuilder(LogBuilder.LEVEL_ERROR, LogBuilder.REQUEST_OUT)
                .withSource(poolName)
                .withMethod(method)
                .withStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .withUrl(url)
                .withException(e);

        if (headers != null) {
            logBuilder.withHeaders(headers.toString());
        }

        if (!StringUtils.isEmpty(params)) {
            logBuilder.withParams(params);
        }

        LOG.error(logBuilder.build(), e);
    }

    /**
     * Log Request
     *
     * @param request Spark request object
     */
    public static void logRequest(final Request request) {
        RequestLogTranslator requestLogTranslator = new RequestLogTranslator(request);

        LogBuilder logBuilder = new LogBuilder(LogBuilder.LEVEL_INFO, LogBuilder.REQUEST_IN)
                .withMethod(requestLogTranslator.getMethod())
                .withUrl(requestLogTranslator.getUrl())
                .withUserAgent(requestLogTranslator.getUserAgent());

        if (null != requestLogTranslator.getSessionId()){
            logBuilder.withSessionId(requestLogTranslator.getSessionId());
        }

        final Optional<String> optQueryParams = requestLogTranslator.getQueryParams();
        optQueryParams.ifPresent(queryParams -> logBuilder.withParams(queryParams));

        final Optional<String> optBody = requestLogTranslator.getBody();
        optBody.ifPresent(body -> logBuilder.withMessage("BODY: " + body));

        LOG.info(logBuilder.build());
    }

    private static String convertQueryParam(List<NameValuePair> queryParams) {
        //TODO remove access token
        return queryParams.toString().replace("[", "{").replace("]", "}");
    }
}
