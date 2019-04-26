package com.mercadolibre.service;

import com.mercadolibre.api.PublicKeyAPI;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.public_key.PublicKeyInfo;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.utils.Either;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;

public enum AuthService {

    INSTANCE;

    private static final Logger LOG = Logger.getLogger(AuthService.class);
    private static final String PUBLIC_KEY_TEST_PREFIX = "test";

    /**
     * Hace el API call a la API de Public Key usando el public key id y obtiene la data asociada a ese id.
     *
     * @param requestId request id
     * @param publicKey public key id
     * @return el objeto public key info
     * @throws ApiException si falla el api call (status code is not 2xx)
     */
    public PublicKeyInfo getPublicKey(@Nonnull final String requestId, final String publicKey) throws ApiException {
        final Either<PublicKeyInfo, ApiError> pk = PublicKeyAPI.INSTANCE.getById(requestId, publicKey);
        if (!pk.isValuePresent()) {
            throw new ApiException(pk.getAlternative());
        }
        return pk.getValue();
    }
}
