package com.mercadolibre.dto.payment;

import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.dto.public_key.PublicKeyInfo;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.utils.HeadersUtils;

/**
 * Objeto con toda la informacion necesaria para hacer el request a payments
 */
public class PaymentRequest {

    private Headers headers;
    private long callerId;
    private long clientId;
    private String userAgent;

    private PaymentBody body;

    public PaymentRequest(final Headers headers, final PaymentRequestBody paymentRequestBody,
                          final PublicKeyInfo publicKey, final Preference preference, final String requestId) {
        this.headers = HeadersUtils.completePaymentHeaders(headers, paymentRequestBody.getToken(),
                requestId, HeadersUtils.isTestToken(paymentRequestBody.getPublicKey()));
        this.callerId = publicKey.getOwnerId();
        this.clientId = publicKey.getClientId();
        this.body = PaymentBody.builder(paymentRequestBody, preference).build();
        this.userAgent = userAgent;
    }

    public long getCallerId() {
        return callerId;
    }

    public long getClientId() {
        return clientId;
    }

    public Headers getHeaders() {
        return headers;
    }

    public PaymentBody getBody() {
        return body;
    }

    public String getUserAgent() {
        return userAgent;
    }
}

