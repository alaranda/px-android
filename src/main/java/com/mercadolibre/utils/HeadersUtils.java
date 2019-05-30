
package com.mercadolibre.utils;

import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.Headers;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import spark.Request;

import javax.annotation.Nonnull;
import java.util.*;

import static com.mercadolibre.constants.HeadersConstants.*;

public enum HeadersUtils {
    ;

    private static final Set<String> PAYMENT_HEADERS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "x-meli-session-id", "x-tracking-id", "x-idempotency-key")));

    private static final String X_CALLER_SCOPE_PAYMENTS = "payments";

    /**
     * Store a Spark request headers in MeLi representation of {@link Headers}.
     *
     * @param request the Spark request from which to obtain the headers.
     * @return a MeLi Headers instance containing the headers
     */
    public static Headers fromSparkHeaders(final Request request) {
        final Headers headers = new Headers();
        request.headers().stream().map(h -> new Header(h, request.headers(h))).forEach(headers::add);
        return headers;
    }

    /**
     * Create a new Headers instance with, at most, the headers which appear in the collection of header names.
     *
     * @param nonFilteredHeaders  the headers to be filtered
     * @param headerNamesToFilter the header names which will be kept in the resulting headers
     * @return the filtered headers
     */
    public static Headers filter(final Headers nonFilteredHeaders, final Collection<String> headerNamesToFilter) {
        final Headers filteredHeaders = new Headers();
        for (final String headerName : headerNamesToFilter) {
            if (nonFilteredHeaders.contains(headerName)) {
                filteredHeaders.add(nonFilteredHeaders.getHeader(headerName));
            }
        }
        return filteredHeaders;
    }

    /**
     * Get the header to allow payments caller scope
     *
     * @return Header with payments caller scope
     */
    public static Header getPaymentsCallerScopes() {
        return new Header(X_CALLER_SCOPES, X_CALLER_SCOPE_PAYMENTS);
    }

    /**
     * Get the test token header
     *
     * @param publicKey id
     * @return Header with the testToken
     */
    public static Header getTestToken(final String publicKey) {
        return new Header(TEST_TOKEN, String.valueOf(isTestToken(publicKey)));
    }

    public static Headers completePaymentHeaders(final Headers headers, final String token,
                                                 final String requestId) {
        //TODO TrackingProductId tiene que venir desde las apps (?)
        Headers filteredHeaders = HeadersUtils.filter(headers, PAYMENT_HEADERS);
        if (!filteredHeaders.contains("x-idempotency-key")) {
            filteredHeaders.add("x-idempotency-key", generateKey(token, requestId));
        }
        filteredHeaders.add(X_REQUEST_ID, requestId);
        filteredHeaders.add(getPaymentsCallerScopes());
        return filteredHeaders;
    }

    private static String generateKey(final String token, final String requestId) {
        StringBuilder builder = new StringBuilder();
        builder.append(requestId);
        if (!StringUtils.isBlank(token)) {
            builder.append(token);
        }
        return builder.toString();
    }

    public static Headers getHeaders(final String requestId) {
        return new Headers()
                .add(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .add(REQUEST_ID, requestId);
    }

    public static String getUserAgent(final Headers headers) {
        String userAgent = null;
        if (headers.contains("X-Mobile-Version")) {
            userAgent = headers.getHeader("X-Mobile-Version").getValue();
        }
        return userAgent;
    }

    private static boolean isTestToken(final String publicKey) {
        return publicKey.startsWith("TEST");
    }
}