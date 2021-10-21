package com.mercadolibre.service;

import com.mercadolibre.api.DaoProvider;
import com.mercadolibre.dto.kyc.UserIdentificationResponse;
import com.mercadolibre.px.constants.HeadersConstants;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.restclient.http.Headers;
import java.util.concurrent.CompletableFuture;

public enum UserIdentificationService {
  INSTANCE;

  private static final String PX_CHECKOUT_MOBILE_PAYMENTS_INITIATIVE =
      "px-checkout-mobile-payments";
  private static final String KYC_VAULT_HEADER_API_SANDBOX_KEY = "kyc.vault.header.api.sandbox";
  private static final String KYC_USER_IDENTIFICATION_QUERY =
      "id, identification { type, number }, registration_identifiers { email { address } }, "
          + "person { other_identifications { type, number }} ";

  private final DaoProvider daoProvider = new DaoProvider();

  public CompletableFuture<Either<UserIdentificationResponse, ApiError>> getAsyncUserIdentification(
      final String userId, final Context context) {
    return daoProvider
        .getKycVaultV2Dao()
        .getAsync(
            context,
            KYC_USER_IDENTIFICATION_QUERY,
            PX_CHECKOUT_MOBILE_PAYMENTS_INITIATIVE,
            userId,
            UserIdentificationResponse.class,
            buildHeaders());
  }

  public UserIdentificationResponse getUserIdentification(
      final String userId, final Context context) throws ApiException {
    return daoProvider
        .getKycVaultV2Dao()
        .getUserResponse(
            context,
            KYC_USER_IDENTIFICATION_QUERY,
            PX_CHECKOUT_MOBILE_PAYMENTS_INITIATIVE,
            userId,
            UserIdentificationResponse.class,
            buildHeaders());
  }

  private Headers buildHeaders() {
    return new Headers()
        .add(
            HeadersConstants.X_API_SANDBOX,
            ConfigurationService.getInstance().getStringByName(KYC_VAULT_HEADER_API_SANDBOX_KEY));
  }
}
