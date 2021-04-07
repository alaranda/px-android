package com.mercadolibre.endpoints;

import static com.mercadolibre.constants.Constants.PAYMENT_ID;
import static com.mercadolibre.px.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.constants.CommonParametersNames.CALLER_SITE_ID;
import static com.mercadolibre.px.constants.CommonParametersNames.CLIENT_ID;
import static com.mercadolibre.px.constants.HeadersConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockPaymentAPI;
import com.mercadolibre.controllers.RemediesController;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseHighRisk;
import com.mercadolibre.px.constants.HeadersConstants;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.exceptions.ValidationException;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.restclient.util.Constants;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

public class RemediesControllerTest {

  private static final String PAYMENT_ID_TEST = "123456789";
  private static final String CALLER_ID_TEST = "11111";
  private static final String CLIENT_ID_TEST = "999999";
  private static final String USER_AGENT_HEADER = "PX/Android/4.23.2";
  private static final String REQUEST_ID_TEST = "REQUEST_ID_TEST";

  private RemediesController remediesController = new RemediesController();

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void getRemedy_withoutPaymentId_400BadRequest() throws ApiException {

    final Request request = Mockito.mock(Request.class);
    when(request.queryParams(CALLER_ID)).thenReturn(CALLER_ID_TEST);
    when(request.queryParams(CLIENT_ID)).thenReturn(CLIENT_ID_TEST);
    when(request.params(PAYMENT_ID)).thenReturn(null);
    when(request.userAgent()).thenReturn(USER_AGENT_HEADER);
    when(request.url()).thenReturn("url-test");

    final HttpServletRequest innerRequest = Mockito.mock(HttpServletRequest.class);
    when(innerRequest.getHeader(HeadersConstants.X_REQUEST_ID))
        .thenReturn(UUID.randomUUID().toString());
    when(innerRequest.getHeader(Constants.X_FORWARDED_HEADER_NAMES)).thenReturn("");
    when(request.raw()).thenReturn(innerRequest);

    final Response response = Mockito.mock(Response.class);

    try {
      final RemediesResponse remediesResponse = remediesController.getRemedy(request, response);
      fail("Error payment id");
    } catch (final ValidationException e) {
      assertThat(e.getDescription(), is("payment id required"));
    }
  }

  @Test
  public void getRemedy_highRiskWithoutPlatform_emptyResponse() throws ApiException, IOException {

    MockPaymentAPI.getPayment(
        PAYMENT_ID_TEST,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/rejected_highRisk.json")));

    final Request request = Mockito.mock(Request.class);
    when(request.params(PAYMENT_ID)).thenReturn(PAYMENT_ID_TEST);
    when(request.queryParams(CALLER_ID)).thenReturn(CALLER_ID_TEST);
    when(request.queryParams(CLIENT_ID)).thenReturn(CLIENT_ID_TEST);
    when(request.attribute(X_REQUEST_ID)).thenReturn(REQUEST_ID_TEST);
    when(request.headers("User-Agent")).thenReturn(USER_AGENT_HEADER);
    when(request.url()).thenReturn("url-test");
    when(request.headers(PLATFORM)).thenReturn("MP");
    when(request.queryParams(CALLER_SITE_ID)).thenReturn(Site.MLA.getSiteId());
    when(request.body())
        .thenReturn(
            IOUtils.toString(getClass().getResourceAsStream("/remedies/remedy_request.json")));

    final HttpServletRequest innerRequest = Mockito.mock(HttpServletRequest.class);
    when(innerRequest.getHeader(HeadersConstants.X_REQUEST_ID))
        .thenReturn(UUID.randomUUID().toString());
    when(innerRequest.getHeader(Constants.X_FORWARDED_HEADER_NAMES)).thenReturn("");
    when(request.raw()).thenReturn(innerRequest);

    final Response response = Mockito.mock(Response.class);

    final RemediesResponse remediesResponse = remediesController.getRemedy(request, response);

    final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
    assertThat(responseHighRisk, is(nullValue()));
  }

  @Test
  public void getRemedy_invalidPlatform_throwsIllegalStateException()
      throws ApiException, IOException {

    MockPaymentAPI.getPayment(
        PAYMENT_ID_TEST,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/rejected_highRisk.json")));

    final Request request = Mockito.mock(Request.class);
    when(request.params(PAYMENT_ID)).thenReturn(PAYMENT_ID_TEST);
    when(request.queryParams(CALLER_ID)).thenReturn(CALLER_ID_TEST);
    when(request.queryParams(CLIENT_ID)).thenReturn(CLIENT_ID_TEST);
    when(request.attribute(X_REQUEST_ID)).thenReturn(REQUEST_ID_TEST);
    when(request.headers("User-Agent")).thenReturn(USER_AGENT_HEADER);
    when(request.url()).thenReturn("url-test");
    when(request.headers(PLATFORM)).thenReturn("DEFINITELY_NOT_A_VALID_PLATFORM");
    when(request.queryParams(CALLER_SITE_ID)).thenReturn(Site.MLA.getSiteId());
    when(request.body())
        .thenReturn(
            IOUtils.toString(getClass().getResourceAsStream("/remedies/remedy_request.json")));

    final HttpServletRequest innerRequest = Mockito.mock(HttpServletRequest.class);
    when(innerRequest.getHeader(HeadersConstants.X_REQUEST_ID))
        .thenReturn(UUID.randomUUID().toString());
    when(innerRequest.getHeader(Constants.X_FORWARDED_HEADER_NAMES)).thenReturn("");
    when(request.raw()).thenReturn(innerRequest);

    final Response response = Mockito.mock(Response.class);

    try {
      remediesController.getRemedy(request, response);
      fail("IllegalStateException was expected");
    } catch (IllegalStateException e) {
      // everything went well
    }
  }
}
