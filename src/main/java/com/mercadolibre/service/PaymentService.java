package com.mercadolibre.service;

import static com.mercadolibre.constants.Constants.*;
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.EXTERNAL_ERROR;
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.INTERNAL_ERROR;

import com.mercadolibre.api.PaymentAPI;
import com.mercadolibre.api.PreferenceAPI;
import com.mercadolibre.api.TedAPI;
import com.mercadolibre.dto.Order;
import com.mercadolibre.dto.PublicKeyAndPreference;
import com.mercadolibre.dto.Ted;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentData;
import com.mercadolibre.dto.payment.PaymentDataBody;
import com.mercadolibre.dto.payment.PaymentRequest;
import com.mercadolibre.dto.payment.PaymentRequestBody;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.dto.lib.user.PublicKey;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.utils.PaymentMethodUtils;
import com.mercadolibre.utils.datadog.DatadogTransactionsMetrics;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpStatus;
import spark.utils.StringUtils;

public enum PaymentService {
  INSTANCE;

  public Payment doPayment(final Context context, final PaymentRequest paymentRequest)
      throws ApiException {
    Either<Payment, ApiError> payment =
        PaymentAPI.INSTANCE.doPayment(
            context,
            paymentRequest.getCallerId(),
            paymentRequest.getClientId(),
            paymentRequest.getBody(),
            paymentRequest.getHeaders());
    if (!payment.isValuePresent()) {
      throw new ApiException(payment.getAlternative());
    }
    return payment.getValue();
  }

  public PaymentRequest getPaymentRequestLegacy(
      final Context context,
      final PaymentRequestBody paymentRequestBody,
      final String publicKeyId,
      final Headers headers)
      throws ApiException, ExecutionException, InterruptedException {

    final PublicKeyAndPreference publicKeyAndPreference =
        getPublicKeyAndPreference(context, publicKeyId, paymentRequestBody.getPrefId());
    final PublicKey publicKeyInfo = publicKeyAndPreference.getPublicKey();
    final Preference preference = publicKeyAndPreference.getPreference();

    return PaymentRequest.Builder.createWhiteLabelLegacyPaymentRequest(
            headers, paymentRequestBody, preference, context.getRequestId())
        .withCallerId(publicKeyInfo.getOwnerId())
        .withClientId(publicKeyInfo.getClientId())
        .withPreference(preference)
        .withHeaderTestToken(publicKeyId)
        .build();
  }

  public PaymentRequest getPaymentRequest(
      final Context context,
      final PaymentDataBody paymentDataBody,
      final String publicKeyId,
      final String callerId,
      final String clientId,
      final Headers headers)
      throws InterruptedException, ApiException, ExecutionException {

    final PublicKeyAndPreference publicKeyAndPreference =
        getPublicKeyAndPreference(context, publicKeyId, paymentDataBody.getPrefId());
    final PublicKey publicKeyInfo = publicKeyAndPreference.getPublicKey();
    final Preference preference = publicKeyAndPreference.getPreference();
    final PaymentData paymentData = paymentDataBody.getPaymentData().get(0);

    if (StringUtils.isNotBlank(callerId)) {
      final Order order =
          setOrder(preference, Long.valueOf(callerId), paymentDataBody.getMerchantOrderId());
      final Boolean isSameBankAccountOwner =
          getIsSameBankAccountOwner(
              context, publicKeyInfo.getOwnerId(), Long.valueOf(callerId), paymentData, preference);
      return createBlackLabelRequest(
          setProductIdPreference(headers, preference),
          paymentData,
          preference,
          publicKeyInfo,
          context.getRequestId(),
          callerId,
          clientId,
          order,
          publicKeyId,
          isSameBankAccountOwner);
    }
    return PaymentRequest.Builder.createWhiteLabelPaymentRequest(
            headers, paymentDataBody.getPaymentData().get(0), preference, context.getRequestId())
        .withCallerId(publicKeyInfo.getOwnerId())
        .withClientId(publicKeyInfo.getClientId())
        .withPreference(preference)
        .withHeaderTestToken(publicKeyId)
        .build();
  }

  private PublicKeyAndPreference getPublicKeyAndPreference(
      final Context context, final String publicKeyId, final String prefId)
      throws ApiException, ExecutionException, InterruptedException {

    final CompletableFuture<Either<PublicKey, ApiError>> futurePk =
        AuthService.INSTANCE.getAsyncPublicKey(context, publicKeyId);
    final CompletableFuture<Either<Preference, ApiError>> futurePref =
        PreferenceAPI.INSTANCE.geAsynctPreference(context, prefId);

    CompletableFuture.allOf(futurePk, futurePref);

    if (!futurePk.get().isValuePresent()) {
      final ApiError apiError = futurePk.get().getAlternative();
      throw new ApiException(EXTERNAL_ERROR, API_CALL_PUBLIC_KEY_FAILED, apiError.getStatus());
    }
    if (!futurePref.get().isValuePresent()) {
      final ApiError apiError = futurePref.get().getAlternative();
      throw new ApiException(EXTERNAL_ERROR, API_CALL_PREFERENCE_FAILED, apiError.getStatus());
    }

    final PublicKey publicKey = futurePk.get().getValue();
    final Preference preference = futurePref.get().getValue();

    return new PublicKeyAndPreference(publicKey, preference);
  }

  private Boolean getIsSameBankAccountOwner(
      final Context context,
      final Long collectorId,
      final Long payerId,
      final PaymentData paymentData,
      final Preference preference)
      throws ApiException, ExecutionException, InterruptedException {

    String paymentMethodId = PaymentMethodUtils.getPaymentMethodId(paymentData, preference);
    if (!PIX_PAYMENT_METHOD_ID.equals(paymentMethodId)) {
      return Boolean.FALSE;
    }

    final CompletableFuture<Either<Ted, ApiError>> futureCollectorTed =
        TedAPI.INSTANCE.getAsyncTed(context, collectorId);
    final CompletableFuture<Either<Ted, ApiError>> futurePayerTed =
        TedAPI.INSTANCE.getAsyncTed(context, payerId);

    CompletableFuture.allOf(futureCollectorTed, futurePayerTed);

    if (!futureCollectorTed.get().isValuePresent()) {
      final ApiError apiError = futureCollectorTed.get().getAlternative();
      throw new ApiException(EXTERNAL_ERROR, API_CALL_TED_FAILED, apiError.getStatus());
    }
    if (!futurePayerTed.get().isValuePresent()) {
      final ApiError apiError = futurePayerTed.get().getAlternative();
      throw new ApiException(EXTERNAL_ERROR, API_CALL_TED_FAILED, apiError.getStatus());
    }

    final Ted collectorTed = futureCollectorTed.get().getValue();
    final Ted payerTed = futurePayerTed.get().getValue();

    return collectorTed.getIdentificationType().equals(payerTed.getIdentificationType())
        && collectorTed.getIdentificationNumber().equals(payerTed.getIdentificationNumber());
  }

  private PaymentRequest createBlackLabelRequest(
      final Headers headers,
      final PaymentData paymentData,
      final Preference preference,
      final PublicKey publicKey,
      final String requestId,
      final String callerId,
      final String clientId,
      final Order order,
      final String pubicKeyId,
      final Boolean isSameBankAccountOwner) {

    return PaymentRequest.Builder.createBlackLabelPaymentRequest(
            headers, paymentData, preference, requestId, isSameBankAccountOwner)
        .withCallerId(Long.valueOf(callerId))
        .withClientId(Long.valueOf(clientId))
        .withPreference(preference)
        .withCollector(publicKey.getOwnerId(), preference.getOperatorIdCollector())
        .withOrder(order)
        .withHeaderTestToken(pubicKeyId)
        .build();
  }

  private Order setOrder(
      final Preference preference, final Long payerId, final Long merchantOrderId)
      throws ApiException {
    if (String.valueOf(payerId).equals(preference.getCollectorId())) {
      throw new ApiException(INTERNAL_ERROR, "Payer equals Collector", HttpStatus.SC_BAD_REQUEST);
    }

    if (null != merchantOrderId) {
      DatadogTransactionsMetrics.addOrderTypePayment(MERCHANT_ORDER);
      return Order.createOrderMP(merchantOrderId);
    }

    if (null != preference.getMerchantOrderId()) {
      DatadogTransactionsMetrics.addOrderTypePayment(MERCHANT_ORDER);
      return Order.createOrderMP(preference.getMerchantOrderId());
    }

    if (null != preference.getOrderId()) {
      DatadogTransactionsMetrics.addOrderTypePayment(ORDER);
      return Order.createOrderML(preference.getOrderId());
    }

    DatadogTransactionsMetrics.addOrderTypePayment(WITHOUT_ORDER);
    return null;
  }

  private Headers setProductIdPreference(final Headers headers, final Preference preference) {

    if (null == preference || null == preference.getProductId()) return headers;

    headers.add(PRODUCT_ID, preference.getProductId());
    return headers;
  }
}
