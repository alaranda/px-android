package com.mercadolibre.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.user.PublicKey;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

public class PublicKeyAPITest extends RestClientTestBase {

  private final String PUBLIC_KEY_FAIL = "public-key-id";
  private final String PUBLIC_KEY_OK = "APP_USR-ba2e6b8c-8b6d-4fc3-8a47-0ab241d0dba4";
  private final Context context = Context.builder().requestId(UUID.randomUUID().toString()).build();
  private final PublicKeyAPI publicKeyAPI = PublicKeyAPI.INSTANCE;

  @Before
  public void setUp() {
    RequestMockHolder.clear();
  }

  @Test
  public void getPublicKey_fail()
      throws IOException, ApiException, ExecutionException, InterruptedException {
    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_FAIL,
        HttpStatus.SC_INTERNAL_SERVER_ERROR,
        IOUtils.toString(getClass().getResourceAsStream("/publicKey/invalidPK.json")));
    CompletableFuture<Either<PublicKey, ApiError>> futurePublicKey =
        publicKeyAPI.getAsyncById(context, PUBLIC_KEY_FAIL);
    assertFalse(futurePublicKey.get().isValuePresent());
    assertNotNull(futurePublicKey.get().getAlternative());
  }

  @Test
  public void getPublicKey_ok()
      throws IOException, ApiException, ExecutionException, InterruptedException {
    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_OK,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream(String.format("/publicKey/%s.json", PUBLIC_KEY_OK))));

    CompletableFuture<Either<PublicKey, ApiError>> futurePublicKey =
        publicKeyAPI.getAsyncById(context, PUBLIC_KEY_OK);
    assertTrue(futurePublicKey.get().isValuePresent());
  }
}
