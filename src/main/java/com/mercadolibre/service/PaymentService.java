package com.mercadolibre.service;

import com.mercadolibre.api.PaymentAPI;
import com.mercadolibre.api.PreferenceAPI;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.PublicKeyAndPreference;
import com.mercadolibre.dto.access_token.AccessToken;
import com.mercadolibre.dto.merchant_orders.MerchantOrder;
import com.mercadolibre.dto.payment.*;
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

    public PaymentRequest getPaymentRequestLegacy(final PaymentRequestBody paymentRequestBody, final String publicKeyId, final String requestId, final Headers headers) throws ApiException, ExecutionException, InterruptedException {

        final PublicKeyAndPreference publicKeyAndPreference = getPublicKeyAndPreference(publicKeyId, paymentRequestBody.getPrefId(), requestId);
        final PublicKeyInfo publicKeyInfo = publicKeyAndPreference.getPublicKey();
        final Preference preference = publicKeyAndPreference.getPreference();

        return PaymentRequest.Builder.createWhiteLabelLegacyPaymentRequest(headers, paymentRequestBody, preference, requestId)
                .withCallerId(publicKeyInfo.getOwnerId())
                .withClientId(publicKeyInfo.getClientId())
                .withHeaderTestToken(publicKeyId)
                .build();
    }

    public PaymentRequest getPaymentRequest(final PaymentDataBody paymentDataBody, final String publicKeyId,
                                            final String callerId, final String clientId, final String requestId, final Headers headers) throws InterruptedException, ApiException, ExecutionException {

        final PublicKeyAndPreference publicKeyAndPreference = getPublicKeyAndPreference(publicKeyId, paymentDataBody.getPrefId(), requestId);
        final PublicKeyInfo publicKeyInfo = publicKeyAndPreference.getPublicKey();
        final Preference preference = publicKeyAndPreference.getPreference();

        if (StringUtils.isNotBlank(callerId)) {
            final MerchantOrder merchantOrder = MerchantOrderService.INSTANCE.createMerchantOrder(requestId, preference, Long.valueOf(callerId));
            return createBlackLabelRequest(headers, paymentDataBody.getPaymentData().get(0), preference, publicKeyInfo, requestId, callerId, clientId ,merchantOrder, publicKeyId);
        }
        return PaymentRequest.Builder.createWhiteLabelPaymentRequest(headers, paymentDataBody.getPaymentData().get(0), preference, requestId)
                .withCallerId(publicKeyInfo.getOwnerId())
                .withClientId(publicKeyInfo.getClientId())
                .withHeaderTestToken(publicKeyId)
                .build();
    }

    private PublicKeyAndPreference getPublicKeyAndPreference(final String publicKeyId, final String prefId,
                                                             final String requestId) throws ApiException, ExecutionException, InterruptedException {

        final CompletableFuture<Either<PublicKeyInfo, ApiError>> futurePk =
                AuthService.INSTANCE.getAsyncPublicKey(publicKeyId, requestId);
        final CompletableFuture<Either<Preference, ApiError>> futurePref =
                PreferenceAPI.INSTANCE.geAsynctPreference(prefId, requestId);

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

        return new PublicKeyAndPreference(publicKey, preference);
    }

    private PaymentRequest createBlackLabelRequest(final Headers headers, final PaymentData paymentData,
                                                   final Preference preference, final PublicKeyInfo publicKey,
                                                   final String requestId, final String callerId, final String clientId,
                                                   final MerchantOrder merchantOrder, final String pubicKeyId) {

        return PaymentRequest.Builder.createBlackLabelPaymentRequest(headers, paymentData, preference, requestId)
                .withCallerId(Long.valueOf(callerId))
                .withClientId(Long.valueOf(clientId))
                .withCollector(publicKey.getOwnerId())
                .withOrder(merchantOrder.getId())
                .withHeaderTestToken(pubicKeyId)
                .build();
    }
}