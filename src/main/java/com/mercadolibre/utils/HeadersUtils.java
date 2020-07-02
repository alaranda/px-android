package com.mercadolibre.utils;

import static com.mercadolibre.px.toolkit.constants.HeadersConstants.*;

import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.Headers;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import spark.Request;

public enum HeadersUtils {
  ;

  private static final Set<String> PAYMENT_HEADERS =
      Collections.unmodifiableSet(
          new HashSet<>(Arrays.asList(MELI_SESSION, TRACKING, IDEMPOTENCY, PRODUCT_ID)));

  private static final String X_CALLER_SCOPE_PAYMENTS = "payments";

  public static final String ONE_TAP = "one_tap";

  // For payments request
  public static final String X_TRACKING_ID_SECURITY = "X-Tracking-Id";
  public static final String AUTHENTICATION_FACTOR_2FA = "security:2fa";
  public static final String AUTHENTICATION_FACTOR_NONE = "security:none";

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
   * Create a new Headers instance with, at most, the headers which appear in the collection of
   * header names.
   *
   * @param nonFilteredHeaders the headers to be filtered
   * @param headerNamesToFilter the header names which will be kept in the resulting headers
   * @return the filtered headers
   */
  public static Headers filter(
      final Headers nonFilteredHeaders, final Collection<String> headerNamesToFilter) {
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

  public static Headers completePaymentHeaders(
      final Headers headers, final String token, final String requestId) {
    // TODO TrackingProductId tiene que venir desde las apps (?)
    Headers filteredHeaders = HeadersUtils.filter(headers, PAYMENT_HEADERS);
    if (!filteredHeaders.contains("x-idempotency-key")) {
      filteredHeaders.add("x-idempotency-key", generateKey(token, requestId));
    }
    filteredHeaders.add(X_REQUEST_ID, requestId);
    filteredHeaders.add(getPaymentsCallerScopes());
    if (null != headers.getHeader(X_SECURITY)) {
      filteredHeaders.add(calculateSecurityHeader(headers));
    }
    return filteredHeaders;
  }

  public static Header calculateSecurityHeader(Headers headers) {
    Header securityHeader = headers.getHeader(X_SECURITY);
    if (securityHeader.getValue().equalsIgnoreCase(AUTHENTICATION_FACTOR_2FA)) {
      return new Header(X_TRACKING_ID_SECURITY, AUTHENTICATION_FACTOR_2FA);
    }
    return new Header(X_TRACKING_ID_SECURITY, AUTHENTICATION_FACTOR_NONE);
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
