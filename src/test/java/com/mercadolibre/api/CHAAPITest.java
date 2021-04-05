package com.mercadolibre.api;

import static com.mercadolibre.px.constants.ErrorCodes.EXTERNAL_ERROR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.mercadolibre.dto.cha.CardHolder;
import com.mercadolibre.dto.cha.CardHolderAuthenticationResponse;
import com.mercadolibre.helper.MockTestHelper;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

/** Card Holder Authentication API Test */
public class CHAAPITest extends RestClientTestBase {

  private final String CARD_TOKEN_TEST = "7cfa4190465bf6104dcb78050d1d6dfa";
  private final CardHolderAuthenticationAPI cardHolderAuthenticationAPI =
      new CardHolderAuthenticationAPI();

  @Before
  public void setUp() {
    RequestMockHolder.clear();
  }

  @Test
  public void authenticateCard_ok() throws ApiException, IOException {
    Context context = MockTestHelper.mockContextLibDto();

    MockCHAAPI.authenticateCard(
        CARD_TOKEN_TEST,
        IOUtils.toString(getClass().getResourceAsStream("/authentication/cha-wrapped.json")),
        HttpStatus.SC_OK);

    final CardHolderAuthenticationResponse fos =
        cardHolderAuthenticationAPI.authenticateCard(
            context,
            CARD_TOKEN_TEST,
            GsonWrapper.fromJson(
                IOUtils.toString(
                    getClass().getResourceAsStream("/authentication/cha-original.json")),
                CardHolder.class));

    assertThat(fos, is(notNullValue()));
  }

  @Test
  public void authenticateCard_fail() throws IOException {
    Context context = MockTestHelper.mockContextLibDto();

    try {
      MockCHAAPI.authenticateCard(
          CARD_TOKEN_TEST,
          IOUtils.toString(getClass().getResourceAsStream("/authentication/cha-wrapped.json")),
          HttpStatus.SC_BAD_REQUEST);

      cardHolderAuthenticationAPI.authenticateCard(
          context,
          CARD_TOKEN_TEST,
          GsonWrapper.fromJson(
              IOUtils.toString(getClass().getResourceAsStream("/authentication/cha-original.json")),
              CardHolder.class));

      fail("Exception is expected");
    } catch (final ApiException e) {
      assertEquals(e.getDescription(), "API call to CHA failed");
      assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
      assertEquals(e.getCode(), EXTERNAL_ERROR);
    }
  }

  @Test
  public void authenticateCard_fail500() throws IOException {
    Context context = MockTestHelper.mockContextLibDto();

    try {
      MockCHAAPI.authenticateCard(
          CARD_TOKEN_TEST,
          IOUtils.toString(getClass().getResourceAsStream("/authentication/cha-wrapped.json")),
          HttpStatus.SC_INTERNAL_SERVER_ERROR);

      cardHolderAuthenticationAPI.authenticateCard(
          context,
          CARD_TOKEN_TEST,
          GsonWrapper.fromJson(
              IOUtils.toString(getClass().getResourceAsStream("/authentication/cha-original.json")),
              CardHolder.class));

      fail("Exception is expected");
    } catch (final ApiException e) {
      assertEquals(e.getDescription(), "API call to CHA failed");
      assertEquals(e.getStatusCode(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
      assertEquals(e.getCode(), EXTERNAL_ERROR);
    }
  }

  @Test
  public void authenticateCard_throwException() throws IOException {
    Context context = MockTestHelper.mockContextLibDto();

    try {
      cardHolderAuthenticationAPI.authenticateCard(
          context,
          CARD_TOKEN_TEST,
          GsonWrapper.fromJson(
              IOUtils.toString(getClass().getResourceAsStream("/authentication/cha-original.json")),
              CardHolder.class));

      fail("Exception is expected");
    } catch (final ApiException e) {
      assertEquals(e.getDescription(), "API call to authenticate card holder failed");
      assertEquals(e.getStatusCode(), HttpStatus.SC_GATEWAY_TIMEOUT);
      assertEquals(e.getCode(), EXTERNAL_ERROR);
    }
  }
}
