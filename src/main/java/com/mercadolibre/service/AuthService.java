package com.mercadolibre.service;

import com.mercadolibre.api.PublicKeyAPI;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.user.PublicKey;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
import java.util.concurrent.CompletableFuture;

public enum AuthService {
  INSTANCE;

  /**
   * Devuelve una promesa de publicKey llamando a PublicKeyAPI
   *
   * @param publicKeyId publicKey id
   * @param context context object
   * @return PublicKey
   * @throws ApiException si falla PublicKeyAPI
   */
  public final CompletableFuture<Either<PublicKey, ApiError>> getAsyncPublicKey(
      final Context context, final String publicKeyId) throws ApiException {
    return PublicKeyAPI.INSTANCE.getAsyncById(context, publicKeyId);
  }

  /**
   * Hace el API call a la API de Public Key usando el callerId y el clientId y obtiene la data
   * asociada a ese id.
   *
   * @param context context object
   * @param preferenceCollectorId preferenceCollectorId
   * @param clientId String
   * @return el objeto public key info
   * @throws ApiException si falla el api call (status code is not 2xx)
   */
  public PublicKey getPublicKey(
      final Context context, final String preferenceCollectorId, final String clientId)
      throws ApiException {
    final Either<PublicKey, ApiError> pk =
        PublicKeyAPI.INSTANCE.getBycallerIdAndClientId(context, preferenceCollectorId, clientId);
    if (!pk.isValuePresent()) {
      throw new ApiException(pk.getAlternative());
    }
    return pk.getValue();
  }
}
