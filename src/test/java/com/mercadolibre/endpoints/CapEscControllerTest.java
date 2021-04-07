package com.mercadolibre.endpoints;

import static com.mercadolibre.px.constants.HeadersConstants.LANGUAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockFraudApi;
import com.mercadolibre.controllers.CapEscController;
import com.mercadolibre.dto.fraud.ResetStatus;
import com.mercadolibre.px.constants.CommonParametersNames;
import com.mercadolibre.px.constants.HeadersConstants;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.exceptions.ValidationException;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.restclient.util.Constants;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import spark.Request;
import spark.Response;

public class CapEscControllerTest {

  private CapEscController capEscController = new CapEscController();

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void resetCapEsc_resetCap_200() throws ApiException {

    MockFraudApi.resetCapEsc("123", "321", 200);

    final Request request = Mockito.mock(Request.class);
    final Response response = Mockito.mock(Response.class);
    when(request.params("cardId")).thenReturn("123");
    when(request.queryParams(CommonParametersNames.CLIENT_ID)).thenReturn("321");
    when(request.requestMethod()).thenReturn("GET");
    when(request.url()).thenReturn("URL");
    when(request.headers(LANGUAGE)).thenReturn("es-AR");

    final HttpServletRequest innerRequest = Mockito.mock(HttpServletRequest.class);
    when(innerRequest.getHeader(HeadersConstants.X_REQUEST_ID))
        .thenReturn(UUID.randomUUID().toString());
    when(innerRequest.getHeader(Constants.X_FORWARDED_HEADER_NAMES)).thenReturn("");
    when(request.raw()).thenReturn(innerRequest);

    final ResetStatus resetStatus = capEscController.resetCapEsc(request, response);

    assertThat(resetStatus.getStatus(), is("ok"));
  }

  @Test
  public void resetCapEsc_resetCap_400() throws ApiException {

    final Request request = Mockito.mock(Request.class);
    final Response response = Mockito.mock(Response.class);
    when(request.params("cardId")).thenReturn("123");
    when(request.queryParams(CommonParametersNames.CLIENT_ID)).thenReturn(null);
    when(request.requestMethod()).thenReturn("GET");
    when(request.headers(LANGUAGE)).thenReturn("es-AR");
    when(request.url()).thenReturn("URL");

    final HttpServletRequest innerRequest = Mockito.mock(HttpServletRequest.class);
    when(innerRequest.getHeader(HeadersConstants.X_REQUEST_ID))
        .thenReturn(UUID.randomUUID().toString());
    when(innerRequest.getHeader(Constants.X_FORWARDED_HEADER_NAMES)).thenReturn("");
    when(request.raw()).thenReturn(innerRequest);

    try {
      final ResetStatus resetStatus = capEscController.resetCapEsc(request, response);
      fail("ValidationException clientId");
    } catch (ValidationException e) {
      assertThat(e.getDescription(), is("client id is required"));
    }
  }

  @Test
  public void resetCapEsc_resetCap_400_invalidClientId() throws ApiException {

    final Request request = Mockito.mock(Request.class);
    final Response response = Mockito.mock(Response.class);

    final HttpServletRequest innerRequest = Mockito.mock(HttpServletRequest.class);
    when(innerRequest.getHeader(HeadersConstants.X_REQUEST_ID))
        .thenReturn(UUID.randomUUID().toString());
    when(innerRequest.getHeader(Constants.X_FORWARDED_HEADER_NAMES)).thenReturn("");
    when(request.raw()).thenReturn(innerRequest);

    when(request.params("cardId")).thenReturn(null);
    when(request.queryParams(CommonParametersNames.CLIENT_ID)).thenReturn("1234");
    when(request.requestMethod()).thenReturn("GET");
    when(request.headers(LANGUAGE)).thenReturn("es-AR");
    when(request.url()).thenReturn("URL");

    try {
      final ResetStatus resetStatus = capEscController.resetCapEsc(request, response);
      fail("ValidationException cardId");
    } catch (ValidationException e) {
      assertThat(e.getDescription(), is("card_id is required"));
    }
  }
}
