package com.mercadolibre.api;

import static com.mercadolibre.api.InstructionsApi.getPath;
import static com.mercadolibre.px.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.constants.HeadersConstants.X_REQUEST_ID;

import com.mercadolibre.dto.instructions.InstructionsResponse;
import com.mercadolibre.helper.MockTestHelper;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.restclient.MockResponse;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.HttpMethod;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
import java.util.UUID;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

public class InstructionsApiTest extends RestClientTestBase {

  private final InstructionsApi instructionsApi = new InstructionsApi();

  private final String PAYMENT_ID = "1236056012";
  private final String ACCESS_TOKEN =
      "TEST-3792603160086480-033021-54e7ad29181cdcd4da6e7eb49d73f53d-139274850";

  @Before
  public void setUp() {
    RequestMockHolder.clear();
  }

  @Test
  public void testGetInstructionsMLB_OkResponse() throws IOException {
    Context context = MockTestHelper.mockContextLibDto();
    mockResponse(
        IOUtils.toString(getClass().getResourceAsStream("/instructions/MLB_instructions.json")),
        HttpStatus.SC_OK);
    final Either<InstructionsResponse, ApiError> instructionList =
        instructionsApi.getInstructions(context, PAYMENT_ID, ACCESS_TOKEN);
    Assert.assertTrue(instructionList.isValuePresent());
    InstructionsResponse instructionsResponse = instructionList.getValue();
    Assert.assertEquals(1, instructionsResponse.getInstructions().size());
    Assert.assertEquals(2, instructionsResponse.getInstructions().get(0).getInteractions().size());
  }

  @Test
  public void testGetInstructionsMLB_UnauthorizedResponse() throws IOException {
    Context context = MockTestHelper.mockContextLibDto();
    mockResponse(
        IOUtils.toString(getClass().getResourceAsStream("/instructions/MLB_bad_request.json")),
        HttpStatus.SC_UNAUTHORIZED);
    final Either<InstructionsResponse, ApiError> instructionList =
        instructionsApi.getInstructions(context, PAYMENT_ID, ACCESS_TOKEN);
    assertForApiErrorCall(instructionList, HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  public void testGetInstructionsMLB_NoResponse_GatewayTimeout() {
    Context context = MockTestHelper.mockContextLibDto();
    final Either<InstructionsResponse, ApiError> instructionList =
        instructionsApi.getInstructions(context, PAYMENT_ID, ACCESS_TOKEN);
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

  private void mockResponse(String bodyResponse, int httpStatus) {
    MockResponse.builder()
        .withURL(getPath(PAYMENT_ID, ACCESS_TOKEN, "PX/Android/2.1.2").toString())
        .withMethod(HttpMethod.GET)
        .withRequestHeader(X_REQUEST_ID, UUID.randomUUID().toString())
        .withStatusCode(httpStatus)
        .withResponseHeader(ContentType.HEADER_NAME, ContentType.APPLICATION_JSON.toString())
        .withResponseBody(bodyResponse)
        .build();
  }
}
