package com.mercadolibre.api;

import static com.mercadolibre.px.toolkit.constants.ErrorCodes.EXTERNAL_ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.mercadolibre.dto.fraud.ResetStatus;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.util.UUID;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

public class FraudAPITest extends RestClientTestBase {

  private final String CARD_ID = "TEST-card_id";
  private final String CLIENT_ID = "TEST-client_id4";
  private final Context context = Context.builder().requestId(UUID.randomUUID().toString()).build();
  private final FraudApi fraudApi = new FraudApi();

  @Before
  public void setUp() {
    RequestMockHolder.clear();
  }

  @Test
  public void resetCapEsc_ok() throws ApiException {
    MockFraudApi.resetCapEsc(CARD_ID, CLIENT_ID, HttpStatus.SC_OK);
    ResetStatus status = fraudApi.resetCapEsc(context, CARD_ID, CLIENT_ID);
    assertEquals("ok", status.getStatus());
  }

  @Test
  public void getPublicKey_fail() {
    try {
      MockFraudApi.resetCapEsc(CARD_ID, CLIENT_ID, HttpStatus.SC_BAD_GATEWAY);
      ResetStatus status = fraudApi.resetCapEsc(context, CARD_ID, CLIENT_ID);
      fail("Exception is expected");
    } catch (ApiException e) {
      assertEquals(e.getDescription(), "API call to reset cap esc failed");
      assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_GATEWAY);
      assertEquals(e.getCode(), EXTERNAL_ERROR);
    }
  }

  @Test
  public void getPublicKey_throwException() {
    try {
      MockFraudApi.resetCapEsc("some_card_id", "some_client_id", HttpStatus.SC_GATEWAY_TIMEOUT);
      ResetStatus status = fraudApi.resetCapEsc(context, CARD_ID, CLIENT_ID);
      fail("Exception is expected");
    } catch (ApiException e) {
      assertEquals(e.getDescription(), "API call to reset cap esc failed");
      assertEquals(e.getStatusCode(), HttpStatus.SC_GATEWAY_TIMEOUT);
      assertEquals(e.getCode(), EXTERNAL_ERROR);
    }
  }
}
