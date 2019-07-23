package com.mercadolibre.service;

import com.mercadolibre.api.PublicKeyAPI;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.public_key.PublicKeyInfo;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.utils.Either;
import com.newrelic.api.agent.Trace;
import java.util.concurrent.CompletableFuture;

public enum AuthService {

    INSTANCE;

    /**
     * Devuelve una promesa de publicKey llamando a PublicKeyAPI
     *
     * @param publicKeyId publicKey id
     * @param context   context object
     * @return PublicKey
     * @throws ApiException si falla PublicKeyAPI
     */
    @Trace
    public final CompletableFuture<Either<PublicKeyInfo, ApiError>> getAsyncPublicKey(final Context context, final String publicKeyId) throws ApiException {
        return PublicKeyAPI.INSTANCE.getAsyncById(context, publicKeyId);
    }

    /**
     * Hace el API call a la API de Public Key usando el callerId y el clientId y obtiene la data asociada a ese id.
     *
     * @param context   context object
     * @param callerId caller id
     * @param clientId client id
     * @return el objeto public key info
     * @throws ApiException si falla el api call (status code is not 2xx)
     */
    @Trace
    public PublicKeyInfo getPublicKey(final Context context, final String callerId, final Long clientId) throws ApiException {
        final Either<PublicKeyInfo, ApiError> pk = PublicKeyAPI.INSTANCE.getBycallerIdAndClientId(context, callerId, clientId);
        if (!pk.isValuePresent()) {
            throw new ApiException(pk.getAlternative());
        }
        return pk.getValue();
    }
}
