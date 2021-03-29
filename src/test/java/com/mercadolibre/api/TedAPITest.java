package com.mercadolibre.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.mercadolibre.dto.Ted;
import com.mercadolibre.helper.MockTestHelper;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.restclient.RestClientTestBase;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpStatus;
import org.junit.Test;
import spark.utils.IOUtils;

public class TedAPITest extends RestClientTestBase {

  private final Context context = MockTestHelper.mockContextLibDto();
  private final TedAPI tedAPI = TedAPI.INSTANCE;

  @Test
  public void getTed_ok()
      throws IOException, ApiException, ExecutionException, InterruptedException {
    Long userId = 43394340211L;
    MockTedAPI.getTed(
        userId,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/ted/validTedResponse.json")));

    final CompletableFuture<Either<Ted, ApiError>> futureTed = tedAPI.getAsyncTed(context, userId);
    final Ted ted = futureTed.get().getValue();

    assertEquals("CPF", ted.getIdentificationType());
    assertEquals("40877775885", ted.getIdentificationNumber());
  }

  @Test
  public void getTed_notFound()
      throws ApiException, ExecutionException, InterruptedException, IOException {
    Long userId = 43394340211L;
    MockTedAPI.getTed(
        userId,
        HttpStatus.SC_NOT_FOUND,
        IOUtils.toString(getClass().getResourceAsStream("/ted/invalidTedResponse.json")));

    final CompletableFuture<Either<Ted, ApiError>> futureTed = tedAPI.getAsyncTed(context, userId);
    final ApiError error = futureTed.get().getAlternative();

    assertFalse(futureTed.get().isValuePresent());
    assertNotNull(error);
    assertEquals(error.getStatus(), HttpStatus.SC_NOT_FOUND);
    assertEquals(error.getError(), "invalid_id");
    assertEquals(error.getMessage(), "ted for user_id not found");
  }
}
