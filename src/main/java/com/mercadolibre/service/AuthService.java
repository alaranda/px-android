package com.mercadolibre.service;

import com.mercadolibre.api.AccessTokenAPI;
import com.mercadolibre.api.PublicKeyAPI;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.access_token.AccessToken;
import com.mercadolibre.dto.public_key.PublicKeyInfo;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.utils.Either;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public enum AuthService {

    INSTANCE;

    private static final Logger LOG = Logger.getLogger(AuthService.class);
    private static final String PUBLIC_KEY_TEST_PREFIX = "test";

    /**
     * Devuelve una promesa de publicKey llamando a PublicKeyAPI
     *
     * @param publicKeyId publicKey id
     * @param requestId   request id
     * @return PublicKey
     * @throws ApiException si falla PublicKeyAPI
     */
    public final CompletableFuture<Either<PublicKeyInfo, ApiError>> getAsyncPublicKey(final String requestId, final String publicKeyId) throws ApiException {
        return PublicKeyAPI.INSTANCE.getAsyncById(publicKeyId, requestId);
    }

    /**
     * Hace el API call a la API de Access token usando el access token id y obtiene la data asociada a ese id.
     *
     * @param requestId     request id
     * @param accessTokenId access token id
     * @return el objeto access token
     * @throws ApiException si falla el api call (status code is not 2xx)
     */
    public AccessToken getAccessToken(@Nonnull final String requestId, @Nonnull final String accessTokenId) throws ApiException {
        final Either<AccessToken, ApiError> accessToken = AccessTokenAPI.INSTANCE.getById(accessTokenId, requestId);
        if (!accessToken.isValuePresent()) {
            throw new ApiException(accessToken.getAlternative());
        }
        return accessToken.getValue();
    }
}
