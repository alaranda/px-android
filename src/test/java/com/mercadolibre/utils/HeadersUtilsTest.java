package com.mercadolibre.utils;

import static com.mercadolibre.px.toolkit.constants.HeadersConstants.TEST_TOKEN;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.X_CALLER_SCOPES;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.X_REQUEST_ID;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.X_SECURITY;
import static com.mercadolibre.utils.HeadersUtils.AUTHENTICATION_FACTOR_2FA;
import static com.mercadolibre.utils.HeadersUtils.AUTHENTICATION_FACTOR_NONE;
import static com.mercadolibre.utils.HeadersUtils.X_TRACKING_ID_SECURITY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.Headers;
import org.junit.Test;

public class HeadersUtilsTest {

  @Test
  public void testGetPaymentsCallerScope_returnsPaymentsCallerScope() {
    Header result = HeadersUtils.getPaymentsCallerScopes();

    assertThat(result.getName(), is(X_CALLER_SCOPES));
  }

  @Test
  public void testGetTestToken_returnsTestToken() {
    Header result = HeadersUtils.getTestToken("public_key");

    assertThat(result.getName(), is(TEST_TOKEN));
  }

  @Test
  public void testCalculateSecurityHeader_returnsTrackingSecurityHeader2FA() {
    Headers headers = new Headers().add(new Header(X_SECURITY, AUTHENTICATION_FACTOR_2FA));
    Header result = HeadersUtils.calculateSecurityHeader(headers);

    assertThat(result.getName(), is(X_TRACKING_ID_SECURITY));
    assertThat(result.getValue(), is(AUTHENTICATION_FACTOR_2FA));
  }

  @Test
  public void testCalculateSecurityHeader_returnsTrackingSecurityHeaderNone() {
    Headers headers = new Headers().add(new Header(X_SECURITY, AUTHENTICATION_FACTOR_NONE));
    Header result = HeadersUtils.calculateSecurityHeader(headers);

    assertThat(result.getName(), is(X_TRACKING_ID_SECURITY));
    assertThat(result.getValue(), is(AUTHENTICATION_FACTOR_NONE));
  }

  @Test
  public void
      testCalculateSecurityHeader_withNotValidSecurityValue_returnsTrackingSecurityHeaderNone() {
    Headers headers = new Headers().add(new Header(X_SECURITY, "bad_security_value"));
    Header result = HeadersUtils.calculateSecurityHeader(headers);

    assertThat(result.getName(), is(X_TRACKING_ID_SECURITY));
    assertThat(result.getValue(), is(AUTHENTICATION_FACTOR_NONE));
  }

  @Test
  public void
      testCompletePaymentHeaders_withNotValidSecurityValue_returnsNoTrackingSecurityHeader() {
    Headers headers = new Headers().add(new Header(X_REQUEST_ID, "bad_security_value"));
    Headers result = HeadersUtils.completePaymentHeaders(headers, "token", "request_id");

    assertNull(result.getHeader(X_TRACKING_ID_SECURITY));
  }

  @Test
  public void testCompletePaymentHeaders_witValidSecurityValue_returnsNoTrackingSecurityHeader() {
    Headers headers = new Headers().add(new Header(X_SECURITY, AUTHENTICATION_FACTOR_2FA));
    Headers result = HeadersUtils.completePaymentHeaders(headers, "token", "request_id");

    assertNotNull(result.getHeader(X_TRACKING_ID_SECURITY));
    assertThat(result.getHeader(X_TRACKING_ID_SECURITY).getName(), is(X_TRACKING_ID_SECURITY));
    assertThat(result.getHeader(X_TRACKING_ID_SECURITY).getValue(), is(AUTHENTICATION_FACTOR_2FA));
  }
}
