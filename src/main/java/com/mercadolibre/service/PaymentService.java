package com.mercadolibre.service;

import com.mercadolibre.api.PaymentAPI;
import com.mercadolibre.api.PreferenceAPI;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.access_token.AccessToken;
import com.mercadolibre.dto.merchant_orders.MerchantOrder;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentRequest;
import com.mercadolibre.dto.payment.PaymentRequestBody;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.dto.public_key.PublicKeyInfo;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.utils.Either;
import spark.utils.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public enum PaymentService {

    INSTANCE;

    public Payment doPayment(final PaymentRequest paymentRequest) throws ApiException {
        Either<Payment, ApiError> payment = PaymentAPI.INSTANCE.doPayment(paymentRequest.getCallerId(), paymentRequest.getClientId(),
                paymentRequest.getBody(), paymentRequest.getHeaders());
        if (!payment.isValuePresent()) {
            throw new ApiException(payment.getAlternative());
        }
        return payment.getValue();
    }

    public PaymentRequest getPaymentRequest(final PaymentRequestBody paymentRequestBody, final String publicKeyId,
                                            final String accessTokenId, final String requestId, final Headers headers) throws ApiException, ExecutionException, InterruptedException {

        final CompletableFuture<Either<PublicKeyInfo, ApiError>> futurePk =
                AuthService.INSTANCE.getAsyncPublicKey(publicKeyId, requestId);
        final CompletableFuture<Either<Preference, ApiError>> futurePref =
                PreferenceAPI.INSTANCE.geAsynctPreference(paymentRequestBody.getPrefId(), requestId);

        CompletableFuture.allOf(futurePk, futurePref);

        if (!futurePk.get().isValuePresent()) {
            final ApiError apiError = futurePk.get().getAlternative();
            throw new ApiException("external_error", "API call to public key failed", apiError.getStatus());
        }
        if (!futurePref.get().isValuePresent()) {
            final ApiError apiError = futurePref.get().getAlternative();
            throw new ApiException("external_error", "API call to preference failed", apiError.getStatus());
        }

        final PublicKeyInfo publicKey = futurePk.get().getValue();
        final Preference preference = futurePref.get().getValue();

        if (StringUtils.isNotBlank(accessTokenId)) {
            final AccessToken accessToken = AuthService.INSTANCE.getAccessToken(requestId, accessTokenId);
            final MerchantOrder merchantOrder = MerchantOrderService.INSTANCE.createMerchantOrder(requestId, preference, Long.valueOf(accessToken.getUserId()));
            return createBlackLabelRequest(headers, paymentRequestBody, preference, publicKey, requestId, accessToken, merchantOrder);
        }
        return PaymentRequest.builder(headers, paymentRequestBody, preference, requestId, false)
                .withCallerId(publicKey.getOwnerId())
                .withClientId(publicKey.getClientId())
                .withHeaderTestToken(publicKeyId)
                .build();
    }

    private PaymentRequest createBlackLabelRequest(final Headers headers, final PaymentRequestBody paymentRequestBody,
                                                   final Preference preference, final PublicKeyInfo publicKey,
                                                   final String requestId, final AccessToken accessToken,
                                                   final MerchantOrder merchantOrder) {

        return PaymentRequest.builder(headers, paymentRequestBody, preference, requestId, true)
                .withCallerId(Long.valueOf(accessToken.getUserId()))
                .withClientId(accessToken.getClientId())
                .withCollector(publicKey.getOwnerId())
                .withOrder(merchantOrder.getId())
                .withHeaderTestToken(publicKey.getPublicKey())
                .build();
    }
}