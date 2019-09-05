package com.mercadolibre.service;

import com.mercadolibre.api.PaymentAPI;
import com.mercadolibre.api.PreferenceAPI;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.Order;
import com.mercadolibre.dto.PublicKeyAndPreference;
import com.mercadolibre.dto.payment.*;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.dto.public_key.PublicKeyInfo;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.ErrorsConstants;
import com.mercadolibre.utils.datadog.DatadogTransactionsMetrics;
import org.apache.http.HttpStatus;
import spark.utils.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.mercadolibre.constants.Constants.*;

public enum PaymentService {

    INSTANCE;

    public Payment doPayment(final Context context, final PaymentRequest paymentRequest) throws ApiException {
        Either<Payment, ApiError> payment = PaymentAPI.INSTANCE.doPayment(context, paymentRequest.getCallerId(), paymentRequest.getClientId(),
                paymentRequest.getBody(), paymentRequest.getHeaders());
        if (!payment.isValuePresent()) {
            throw new ApiException(payment.getAlternative());
        }
        return payment.getValue();
    }

    public PaymentRequest getPaymentRequestLegacy(final Context context, final PaymentRequestBody paymentRequestBody, final String publicKeyId, final Headers headers) throws ApiException, ExecutionException, InterruptedException {

        final PublicKeyAndPreference publicKeyAndPreference = getPublicKeyAndPreference(context ,publicKeyId, paymentRequestBody.getPrefId());
        final PublicKeyInfo publicKeyInfo = publicKeyAndPreference.getPublicKey();
        final Preference preference = publicKeyAndPreference.getPreference();

        return PaymentRequest.Builder.createWhiteLabelLegacyPaymentRequest(headers, paymentRequestBody, preference, context.getRequestId())
                .withCallerId(publicKeyInfo.getOwnerId())
                .withClientId(publicKeyInfo.getClientId())
                .withHeaderTestToken(publicKeyId)
                .build();
    }

    public PaymentRequest getPaymentRequest(final Context context, final PaymentDataBody paymentDataBody, final String publicKeyId,
                                            final String callerId, final String clientId, final Headers headers) throws InterruptedException, ApiException, ExecutionException {

        final PublicKeyAndPreference publicKeyAndPreference = getPublicKeyAndPreference(context, publicKeyId, paymentDataBody.getPrefId());
        final PublicKeyInfo publicKeyInfo = publicKeyAndPreference.getPublicKey();
        final Preference preference = publicKeyAndPreference.getPreference();

        if (StringUtils.isNotBlank(callerId)) {
            final Order order = setOrder(preference, Long.valueOf(callerId));
            return createBlackLabelRequest(headers, paymentDataBody.getPaymentData().get(0), preference, publicKeyInfo, context.getRequestId(), callerId, clientId ,order , publicKeyId);
        }
        return PaymentRequest.Builder.createWhiteLabelPaymentRequest(headers, paymentDataBody.getPaymentData().get(0), preference, context.getRequestId())
                .withCallerId(publicKeyInfo.getOwnerId())
                .withClientId(publicKeyInfo.getClientId())
                .withHeaderTestToken(publicKeyId)
                .build();
    }

    private PublicKeyAndPreference getPublicKeyAndPreference(final Context context, final String publicKeyId, final String prefId)
            throws ApiException, ExecutionException, InterruptedException {

        final CompletableFuture<Either<PublicKeyInfo, ApiError>> futurePk =
                AuthService.INSTANCE.getAsyncPublicKey(context, publicKeyId);
        final CompletableFuture<Either<Preference, ApiError>> futurePref =
                PreferenceAPI.INSTANCE.geAsynctPreference(context, prefId);

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
                                                   final Order order, final String pubicKeyId) {

        PaymentRequest paymentRequest =  PaymentRequest.Builder.createBlackLabelPaymentRequest(headers, paymentData, preference, requestId)
                .withCallerId(Long.valueOf(callerId))
                .withClientId(Long.valueOf(clientId))
                .withCollector(publicKey.getOwnerId())
                .withOrder(order)
                .withHeaderTestToken(pubicKeyId)
                .build();

        return paymentRequest;
    }

    private Order setOrder(final Preference preference, final long payerId) throws ApiException {
        if (payerId == preference.getCollectorId()) {
            throw  new ApiException(ErrorsConstants.INTERNAL_ERROR, "Payer equals Collector", HttpStatus.SC_BAD_REQUEST);
        }

        if (null != preference.getMerchantOrderId()){
            DatadogTransactionsMetrics.addOrderTypePayment(MERCHANT_ORDER);
            return new Order(preference.getMerchantOrderId(), Constants.MERCHANT_ORDER_TYPE_MP);
        }

        if (null != preference.getOrderId()){
            DatadogTransactionsMetrics.addOrderTypePayment(ORDER);
            return new Order(preference.getOrderId(), Constants.MERCHANT_ORDER_TYPE_ML);
        }

        DatadogTransactionsMetrics.addOrderTypePayment(WITHOUT_ORDER);
        return null;
    }
}