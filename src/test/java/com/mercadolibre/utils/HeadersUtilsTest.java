package com.mercadolibre.utils;

import static com.mercadolibre.px.constants.HeadersConstants.PRODUCT_ID;
import static com.mercadolibre.px.constants.HeadersConstants.TEST_TOKEN;
import static com.mercadolibre.px.constants.HeadersConstants.X_CALLER_SCOPES;
import static com.mercadolibre.px.constants.HeadersConstants.X_REQUEST_ID;
import static com.mercadolibre.px.constants.HeadersConstants.X_SECURITY;
import static com.mercadolibre.utils.HeadersUtils.AUTHENTICATION_FACTOR_2FA;
import static com.mercadolibre.utils.HeadersUtils.AUTHENTICATION_FACTOR_NONE;
import static com.mercadolibre.utils.HeadersUtils.X_TRACKING_ID_SECURITY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.mercadolibre.px.dto.lib.context.UserAgent;
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
    Headers headers = new Headers().add(new Header(X_SECURITY, "2fa"));
    Header result = HeadersUtils.calculateSecurityHeader(headers);

    assertThat(result.getName(), is(X_TRACKING_ID_SECURITY));
    assertThat(result.getValue(), is(AUTHENTICATION_FACTOR_2FA));
  }

  @Test
  public void testCalculateSecurityHeader_returnsTrackingSecurityHeaderNone() {
    Headers headers = new Headers().add(new Header(X_SECURITY, "none"));
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
  public void testCompletePaymentHeaders_withValidSecurityValue_returnsTrackingSecurityHeader2fa() {
    Headers headers = new Headers().add(new Header(X_SECURITY, "2fa"));
    Headers result = HeadersUtils.completePaymentHeaders(headers, "token", "request_id");

    assertNotNull(result.getHeader(X_TRACKING_ID_SECURITY));
    assertThat(result.getHeader(X_TRACKING_ID_SECURITY).getName(), is(X_TRACKING_ID_SECURITY));
    assertThat(result.getHeader(X_TRACKING_ID_SECURITY).getValue(), is(AUTHENTICATION_FACTOR_2FA));
  }

  @Test
  public void testUserAgentFromHeader_withValidUserAgent_returnsValidUserAgent() {
    String userAgentString = "PX/iOS/4.32.4";
    UserAgent userAgent = HeadersUtils.userAgentFromHeader(userAgentString);

    assertEquals(userAgentString, userAgent.toString());
  }

  @Test
  public void testUserAgentFromHeader_withInvalidUserAgent_returnsInvalidUserAgent() {
    String userAgentString = "invalid-user-agent";
    UserAgent userAgent = HeadersUtils.userAgentFromHeader(userAgentString);

    assertEquals("PX/NoOS/0.0", userAgent.toString());
  }

  @Test
  public void testUserAgentFromHeader_withErroneousUserAgent_returnsInvalidUserAgent() {
    String userAgentString = "Tomi/NoOS/1.2.3";
    UserAgent userAgent = HeadersUtils.userAgentFromHeader(userAgentString);

    assertEquals("Tomi/NoOS/0.0", userAgent.toString());
  }

  @Test
  public void testSetXProductIdForCowFlow() {
    String flow = "/checkout_web/logged";
    Headers headers = new Headers();
    HeadersUtils.setProductIdForCowFlows(flow, headers);
    assertEquals(
        HeadersUtils.CHO_PRO_MOBILE_LOGGED_PRODUCT_ID, headers.getHeader(PRODUCT_ID).getValue());
  }

  @Test
  public void testNoOverwriteProductIdHeaderForNoCowFlow() {
    String flow = "/moneyin";
    Headers headers = new Headers();
    headers.add(PRODUCT_ID, "BCD9F527N9Q001QPA2CG");
    HeadersUtils.setProductIdForCowFlows(flow, headers);
    assertEquals("BCD9F527N9Q001QPA2CG", headers.getHeader(PRODUCT_ID).getValue());
  }
}
