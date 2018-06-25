package com.mercadopago.services;

import com.mercadopago.adapters.MPCall;
import com.mercadopago.model.Instructions;
import com.mercadopago.model.PayerIntent;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentBody;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.preferences.CheckoutPreference;

import java.math.BigDecimal;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CheckoutService {

    @POST("/v1/checkout/payment_methods/search/options")
    MPCall<PaymentMethodSearch> getPaymentMethodSearch(@Header("Accept-Language") String locale, @Query("public_key") String publicKey, @Query("amount") BigDecimal amount, @Query("excluded_payment_types") String excludedPaymentTypes, @Query("excluded_payment_methods") String excludedPaymentMethods, @Body PayerIntent payerIntent, @Query("site_id") String siteId, @Query("api_version") String apiVersion, @Query("processing_mode") String processingMode);

    @POST("/v1/checkout/payments")
    MPCall<Payment> createPayment(@Header("X-Idempotency-Key") String transactionId, @Body PaymentBody body);

    @GET("/v1/checkout/payments/{payment_id}/results")
    MPCall<Instructions> getPaymentResult(@Header("Accept-Language") String locale, @Path(value = "payment_id", encoded = true) Long paymentId, @Query("public_key") String mKey, @Query("access_token") String privateKey, @Query("payment_type") String paymentTypeId, @Query("api_version") String apiVersion);

    @GET("/v1/checkout/preferences/{preference_id}")
    MPCall<CheckoutPreference> getPreference(@Path(value = "preference_id", encoded = true) String checkoutPreferenceId, @Query("public_key") String publicKey);
}