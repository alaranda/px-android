package com.mercadolibre.api;

import static com.mercadolibre.px.constants.ErrorCodes.EXTERNAL_ERROR;

import com.mercadolibre.dto.instructions.Instruction;
import com.mercadolibre.dto.instructions.InstructionsResponse;
import com.mercadolibre.helper.MockTestHelper;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

public class InstructionsApiTest extends RestClientTestBase {

  private final String PAYMENT_ID = "1236056012";
  private final String ACCESS_TOKEN =
      "TEST-3792603160086480-033021-54e7ad29181cdcd4da6e7eb49d73f53d-139274850";
  private final String PUBLIC_KEY = "TEST-d1a694aa-b0ee-4dd2-8326-79b1d53a676o";
  private final String PAYMENT_TYPE_ID = "ticket";

  @Before
  public void setUp() {
    RequestMockHolder.clear();
  }

  @Test
  public void testGetInstructionsMLB_OkResponse() throws IOException {
    Context context = MockTestHelper.mockContextLibDto();
    MockInstructionsAPI.getInstructions(
        PAYMENT_ID,
        ACCESS_TOKEN,
        PUBLIC_KEY,
        PAYMENT_TYPE_ID,
        IOUtils.toString(getClass().getResourceAsStream("/instructions/MLB_instructions.json")),
        HttpStatus.SC_OK);
    final Either<InstructionsResponse, ApiError> instructionList =
        InstructionsApi.INSTANCE.getInstructions(
            context, PAYMENT_ID, ACCESS_TOKEN, PUBLIC_KEY, PAYMENT_TYPE_ID);
    Assert.assertTrue(instructionList.isValuePresent());
    List<Instruction> instructionsResponse = instructionList.getValue().getInstructions();
    Assert.assertEquals(2, instructionsResponse.get(0).getInteractions().size());
  }

  @Test
  public void testGetInstructionsMLB_UnauthorizedResponse() throws IOException {
    Context context = MockTestHelper.mockContextLibDto();
    MockInstructionsAPI.getInstructions(
        PAYMENT_ID,
        ACCESS_TOKEN,
        PUBLIC_KEY,
        PAYMENT_TYPE_ID,
        IOUtils.toString(getClass().getResourceAsStream("/instructions/MLB_bad_request.json")),
        HttpStatus.SC_UNAUTHORIZED);
    final Either<InstructionsResponse, ApiError> instructionList =
        InstructionsApi.INSTANCE.getInstructions(
            context, PAYMENT_ID, ACCESS_TOKEN, PUBLIC_KEY, PAYMENT_TYPE_ID);
    assertForApiErrorCall(instructionList, HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  public void testGetInstructionsMLB_NoResponse_GatewayTimeout() {
    Context context = MockTestHelper.mockContextLibDto();
    final Either<InstructionsResponse, ApiError> instructionList =
        InstructionsApi.INSTANCE.getInstructions(
            context, PAYMENT_ID, ACCESS_TOKEN, PUBLIC_KEY, PAYMENT_TYPE_ID);
    assertForApiErrorCall(instructionList, HttpStatus.SC_GATEWAY_TIMEOUT);
  }

  private void assertForApiErrorCall(
      Either<InstructionsResponse, ApiError> instructionList, int httpStatus) {
    Assert.assertNotNull(instructionList.getAlternative());
    ApiError apiError = instructionList.getAlternative();
    Assert.assertEquals(httpStatus, apiError.getStatus());
    Assert.assertEquals("API call to instructions failed", apiError.getError());
    Assert.assertEquals(EXTERNAL_ERROR, apiError.getMessage());
  }
}
