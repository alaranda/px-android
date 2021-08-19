package com.mercadolibre.service;

import static com.mercadolibre.helper.MockTestHelper.mockContextLibDto;
import static org.junit.Assert.*;

import com.mercadolibre.api.MockKycVaultV2Dao;
import com.mercadolibre.dto.kyc.UserIdentification;
import com.mercadolibre.dto.kyc.UserIdentificationResponse;
import com.mercadolibre.px.constants.HeadersConstants;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.util.concurrent.CompletableFuture;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

public class UserIdentificationServiceTest {

  private static final String USER_ID = "22314151";
  private static final String PX_CHECKOUT_MOBILE_PAYMENTS_INITIATIVE =
      "px-checkout-mobile-payments";
  private static final String KYC_VAULT_HEADER_API_SANDBOX_KEY = "kyc.vault.header.api.sandbox";
  private static final Context CONTEXT = mockContextLibDto();

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  @Test
  public void testGetAsync_whenSuccess() throws Exception {
    MockKycVaultV2Dao.getAsync(
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/kyc/user_22314151_cuil_20147360194.json")),
        (request, response, e) -> {
          assertEquals(
              PX_CHECKOUT_MOBILE_PAYMENTS_INITIATIVE,
              request.getHeader(HeadersConstants.X_PEGASUS_TOKEN).getValue());
          assertEquals(
              ConfigurationService.getInstance().getStringByName(KYC_VAULT_HEADER_API_SANDBOX_KEY),
              request.getHeader(HeadersConstants.X_API_SANDBOX).getValue());
        });

    CompletableFuture<Either<UserIdentificationResponse, ApiError>> asyncUserIdentification =
        UserIdentificationService.INSTANCE.getAsyncUserIdentification(USER_ID, CONTEXT);

    Either<UserIdentificationResponse, ApiError> either = asyncUserIdentification.get();

    assertTrue(either.isValuePresent());

    UserIdentificationResponse userIdentificationResponse = either.getValue();
    UserIdentification userIdentification = userIdentificationResponse.getData().getUser();

    assertEquals(USER_ID, userIdentification.getId().toString());
    assertEquals("CUIL", userIdentification.getIdentification().getType());
    assertEquals("20147360194", userIdentification.getIdentification().getNumber());
  }
}
