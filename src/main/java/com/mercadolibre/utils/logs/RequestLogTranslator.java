package com.mercadolibre.utils.logs;

import com.mercadolibre.utils.AccessTokenUtils;
import org.apache.commons.lang.StringUtils;
import spark.Request;

import java.util.Optional;
import java.util.regex.Matcher;

import static com.mercadolibre.constants.HeadersConstants.SESSION_ID;


/**
 * Translates the request object into strings for logging
 */
public class RequestLogTranslator {
    final private String method;
    final private String body;
    final private String queryParams;
    final private String url;
    final private String userAgent;
    final private String sessionId;

    /**
     * Constructor
     *
     * @param request Spark Request object
     */
    public RequestLogTranslator(final Request request) {
        this.method = request.requestMethod();
        this.body = request.body();
        this.queryParams = request.queryString();
        this.url = request.url();
        this.userAgent = request.userAgent();
        this.sessionId = request.headers(SESSION_ID);
    }

    /**
     * Gets body as string for logging
     *
     * @return Optional of string
     */
    public Optional<String> getBody() {
        if (!StringUtils.isBlank(body)) {

            Matcher atJsonMatcher = AccessTokenUtils.ACCESS_TOKEN_JSON_PARAM_PATTERN.matcher(body);
            if (atJsonMatcher.find()) {
                final String bodyPublicAccessToken = AccessTokenUtils.hideAccessTokenSensitiveData(atJsonMatcher.group(1));
                return Optional.of(atJsonMatcher.replaceAll("\"access_token\"" + ":\"" + bodyPublicAccessToken + "\""));
            }

            return Optional.of(body);
        }
        return Optional.empty();
    }

    /**
     * Gets query params as string for logging
     *
     * @return Optional of String
     */
    public Optional<String> getQueryParams() {
        if (queryParams != null) {
            Matcher matcher = AccessTokenUtils.ACCESS_TOKEN_QUERY_PARAM_PATTERN.matcher(queryParams);
            if (matcher.find()) {
                final String accessToken = matcher.group(0);
                final String publicAccessToken = AccessTokenUtils.hideAccessTokenSensitiveData(accessToken);

                return Optional.of(matcher.replaceAll(publicAccessToken));
            }

            return Optional.of(queryParams);
        }

        return Optional.empty();
    }

    /**
     * Returns HTTP request method
     *
     * @return HTTP request method string
     */
    public String getMethod() {
        return method;
    }

    /**
     * Returns requested URL
     *
     * @return URL
     */
    public String getUrl() {
        return url;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getSessionId() { return sessionId; }
}
