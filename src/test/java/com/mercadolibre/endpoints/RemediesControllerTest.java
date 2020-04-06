package com.mercadolibre.endpoints;

import static com.mercadolibre.constants.Constants.PAYMENT_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_SITE_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CLIENT_ID;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.PLATFORM;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.REQUEST_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockPaymentAPI;
import com.mercadolibre.controllers.RemediesController;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseCallForAuth;
import com.mercadolibre.dto.remedy.ResponseHighRisk;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
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
  public void getRemedy_statusDetailCallForAuthIcbc_200RemedyCallForAuth()
      throws ApiException, IOException {

    MockPaymentAPI.getPayment(
        PAYMENT_ID_TEST,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/11111_callForAuth.json")));

    final Request request = Mockito.mock(Request.class);
    when(request.params(PAYMENT_ID)).thenReturn(PAYMENT_ID_TEST);
    when(request.queryParams(CALLER_ID)).thenReturn(CALLER_ID_TEST);
    when(request.queryParams(CLIENT_ID)).thenReturn(CLIENT_ID_TEST);
    when(request.attribute(REQUEST_ID)).thenReturn(REQUEST_ID_TEST);
    when(request.userAgent()).thenReturn(USER_AGENT_HEADER);
    when(request.url()).thenReturn("url-test");
    when(request.body())
        .thenReturn(
            IOUtils.toString(getClass().getResourceAsStream("/remedies/remedy_request.json")));
    final Response response = Mockito.mock(Response.class);

    final RemediesResponse remediesResponse = remediesController.getRemedy(request, response);

    final ResponseCallForAuth responseCallForAuth = remediesResponse.getCallForAuth();
    assertThat(responseCallForAuth.getTitle(), is("Tu visa ICBC **** 4444 no autorizo el pago"));
    assertThat(
        responseCallForAuth.getMessage(),
        is("Llama a ICBC para autorizar 123.00 a Mercado Pago o paga de otra forma."));
  }

  @Test
  public void getRemedy_withoutPaymentId_400BadRequest() throws ApiException {

    final Request request = Mockito.mock(Request.class);
    when(request.queryParams(CALLER_ID)).thenReturn(CALLER_ID_TEST);
    when(request.queryParams(CLIENT_ID)).thenReturn(CLIENT_ID_TEST);
    when(request.params(PAYMENT_ID)).thenReturn(null);
    when(request.userAgent()).thenReturn(USER_AGENT_HEADER);
    when(request.url()).thenReturn("url-test");
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
        IOUtils.toString(getClass().getResourceAsStream("/payment/11111_highRisk.json")));

    final Request request = Mockito.mock(Request.class);
    when(request.params(PAYMENT_ID)).thenReturn(PAYMENT_ID_TEST);
    when(request.queryParams(CALLER_ID)).thenReturn(CALLER_ID_TEST);
    when(request.queryParams(CLIENT_ID)).thenReturn(CLIENT_ID_TEST);
    when(request.attribute(REQUEST_ID)).thenReturn(REQUEST_ID_TEST);
    when(request.userAgent()).thenReturn(USER_AGENT_HEADER);
    when(request.url()).thenReturn("url-test");
    when(request.headers(PLATFORM)).thenReturn("MP");
    when(request.queryParams(CALLER_SITE_ID)).thenReturn(Site.MLA.getSiteId());
    when(request.body())
        .thenReturn(
            IOUtils.toString(getClass().getResourceAsStream("/remedies/remedy_request.json")));
    final Response response = Mockito.mock(Response.class);

    final RemediesResponse remediesResponse = remediesController.getRemedy(request, response);

    final ResponseHighRisk responseHighRisk = remediesResponse.getHighRisk();
    assertThat(responseHighRisk, is(nullValue()));
  }
}
