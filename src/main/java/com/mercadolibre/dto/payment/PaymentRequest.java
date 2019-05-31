package com.mercadolibre.dto.payment;

import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.utils.HeadersUtils;

/**
 * Objeto con toda la informacion necesaria para hacer el request a payments
 */
public class PaymentRequest {

    private Headers headers;
    private long callerId;
    private long clientId;
    private PaymentBody body;

    private PaymentRequest(final Builder builder) {
        this.headers = builder.headers;
        this.callerId = builder.callerId;
        this.clientId = builder.clientId;
        this.body = builder.body.build();
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

    public static Builder builder(final Headers headers, final PaymentRequestBody paymentRequestBody,
                                  final Preference preference, final String requestId, final boolean isBLacklabel) {
        return new Builder(headers, paymentRequestBody, preference, requestId, isBLacklabel);
    }

    public static final class Builder {

        private Headers headers;
        private long callerId;
        private long clientId;
        private PaymentBody.Builder body;

        Builder(final Headers headers, final PaymentRequestBody paymentRequestBody,
                final Preference preference, final String requestId, final boolean isBLacklabel){
            this.headers = HeadersUtils.completePaymentHeaders(headers, paymentRequestBody.getToken(),
                    requestId);
            this.body = PaymentBody.builder(paymentRequestBody, preference, isBLacklabel);
        }

        public Builder withCallerId(final long callerId) {
            this.callerId = callerId;
            return this;
        }

        public Builder withClientId(final long clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withCollector(final long collectorId) {
            body.withCollector(collectorId);
            return this;
        }

        public Builder withHeaderTestToken(final String publicKey) {
            headers.add(HeadersUtils.getTestToken(publicKey));
            return this;
        }

        public PaymentRequest build() {
            return new PaymentRequest(this);
        }
    }
}

