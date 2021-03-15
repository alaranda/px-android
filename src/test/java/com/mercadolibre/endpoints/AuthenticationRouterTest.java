package com.mercadolibre.endpoints;

import static com.mercadolibre.constants.QueryParamsConstants.CARD_TOKEN;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.ACCESS_TOKEN;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.BAD_REQUEST;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.REQUEST_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockCHAAPI;
import com.mercadolibre.controllers.AuthenticationController;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
import java.util.UUID;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import spark.Request;
import spark.utils.IOUtils;

public class AuthenticationRouterTest {

  private final String CARD_TOKEN_TEST = "7cfa4190465bf6104dcb78050d1d6dfa";
  private final String ACCESS_TOKEN_TEST =
      "TEST_USR-6519316523937252-070516-964cafe7e2c91a2c740155fcb5474280__LA_LD__-261748045";
  private final String CALLER_ID_TEST = "261748045";
  private final String CHA_URL = "/px_mobile/authentication/v1/card_holder";

  private final AuthenticationController authenticationController = new AuthenticationController();

  @Before
  public void before() throws IOException {
    RequestMockHolder.clear();

    MockCHAAPI.authenticateCard(
        CARD_TOKEN_TEST,
        IOUtils.toString(getClass().getResourceAsStream("/authentication/cha-wrapped.json")),
        HttpStatus.SC_OK);
  }

  @Test
  public void authenticateCardHolder_authenticateCHA_200() throws IOException, ApiException {

    final Request request = Mockito.mock(Request.class);
    final spark.Response response = Mockito.mock(spark.Response.class);
    when(request.body())
        .thenReturn(
            IOUtils.toString(getClass().getResourceAsStream("/authentication/cha-wrapped.json")));
    when(request.queryParams(CARD_TOKEN)).thenReturn(CARD_TOKEN_TEST);
    when(request.queryParams(ACCESS_TOKEN)).thenReturn(ACCESS_TOKEN_TEST);
    when(request.requestMethod()).thenReturn(HttpMethod.POST.name());
    when(request.attribute(REQUEST_ID)).thenReturn(UUID.randomUUID().toString());
    when(request.url()).thenReturn(CHA_URL);
    when(request.queryParams(CALLER_ID)).thenReturn(CALLER_ID_TEST);

    final Object fos = authenticationController.authenticateCardHolder(request, response);

    // TODO check value
    assertThat(fos, is(notNullValue()));
  }

  @Test
  public void authenticateCardHolder_withoutCardToken_400() throws IOException {
    try {
      final Request request = Mockito.mock(Request.class);
      final spark.Response response = Mockito.mock(spark.Response.class);
      when(request.body())
          .thenReturn(
              IOUtils.toString(getClass().getResourceAsStream("/authentication/cha-wrapped.json")));
      when(request.queryParams(ACCESS_TOKEN)).thenReturn(ACCESS_TOKEN_TEST);
      when(request.requestMethod()).thenReturn(HttpMethod.POST.name());
      when(request.attribute(REQUEST_ID)).thenReturn(UUID.randomUUID().toString());
      when(request.url()).thenReturn(CHA_URL);
      when(request.queryParams(CALLER_ID)).thenReturn(CALLER_ID_TEST);

      authenticationController.authenticateCardHolder(request, response);

      fail("Exception is expected");
    } catch (final ApiException e) {
      assertEquals(e.getDescription(), "Card token is required");
      assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
      assertEquals(e.getCode(), BAD_REQUEST);
    }
  }

  @Test
  public void authenticateCardHolder_withoutAccessToken_400() throws IOException {

    try {
      final Request request = Mockito.mock(Request.class);
      final spark.Response response = Mockito.mock(spark.Response.class);
      when(request.body())
          .thenReturn(
              IOUtils.toString(getClass().getResourceAsStream("/authentication/cha-wrapped.json")));
      when(request.queryParams(CARD_TOKEN)).thenReturn(CARD_TOKEN_TEST);
      when(request.requestMethod()).thenReturn(HttpMethod.POST.name());
      when(request.attribute(REQUEST_ID)).thenReturn(UUID.randomUUID().toString());
      when(request.url()).thenReturn(CHA_URL);

      authenticationController.authenticateCardHolder(request, response);

      fail("Exception is expected");
    } catch (final ApiException e) {
      assertEquals(e.getDescription(), "Access token is required");
      assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
      assertEquals(e.getCode(), BAD_REQUEST);
    }
  }

  @Test
  public void authenticateCardHolder_invalidAmount_400() throws IOException {
    try {
      final Request request = Mockito.mock(Request.class);
      final spark.Response response = Mockito.mock(spark.Response.class);
      when(request.body())
          .thenReturn(
              IOUtils.toString(
                  getClass().getResourceAsStream("/authentication/cha-invalid-amount.json")));
      when(request.queryParams(CARD_TOKEN)).thenReturn(CARD_TOKEN_TEST);
      when(request.queryParams(ACCESS_TOKEN)).thenReturn(ACCESS_TOKEN_TEST);
      when(request.requestMethod()).thenReturn(HttpMethod.POST.name());
      when(request.attribute(REQUEST_ID)).thenReturn(UUID.randomUUID().toString());
      when(request.url()).thenReturn(CHA_URL);
      when(request.queryParams(CALLER_ID)).thenReturn(CALLER_ID_TEST);

      authenticationController.authenticateCardHolder(request, response);

      fail("Exception is expected");
    } catch (final ApiException e) {
      assertEquals(e.getDescription(), "Error parsing purchase amount");
      assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
      assertEquals(e.getCode(), BAD_REQUEST);
    }
  }

  @Test
  public void authenticateCardHolder_wrongBody_400() throws IOException {
    try {
      final Request request = Mockito.mock(Request.class);
      final spark.Response response = Mockito.mock(spark.Response.class);
      when(request.body())
          .thenReturn(
              IOUtils.toString(
                  getClass().getResourceAsStream("/authentication/cha-wrong-body.json")));
      when(request.queryParams(CARD_TOKEN)).thenReturn(CARD_TOKEN_TEST);
      when(request.queryParams(ACCESS_TOKEN)).thenReturn(ACCESS_TOKEN_TEST);
      when(request.requestMethod()).thenReturn(HttpMethod.POST.name());
      when(request.attribute(REQUEST_ID)).thenReturn(UUID.randomUUID().toString());
      when(request.url()).thenReturn(CHA_URL);
      when(request.queryParams(CALLER_ID)).thenReturn(CALLER_ID_TEST);

      authenticationController.authenticateCardHolder(request, response);

      fail("Exception is expected");
    } catch (final ApiException e) {
      assertEquals(e.getDescription(), "Error parsing body");
      assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
      assertEquals(e.getCode(), BAD_REQUEST);
    }
  }
}
