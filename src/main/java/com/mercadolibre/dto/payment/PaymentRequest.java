package com.mercadolibre.dto.payment;

import com.mercadolibre.dto.Order;
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
    private Preference preference;
    private PaymentBody body;

    private PaymentRequest(final Builder builder) {
        this.headers = builder.headers;
        this.callerId = builder.callerId;
        this.clientId = builder.clientId;
        this.preference = builder.preference;
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

    public Preference getPreference() {
        return preference;
    }

    public PaymentBody getBody() {
        return body;
    }

    public static final class Builder {

        private Headers headers;
        private long callerId;
        private long clientId;
        private Preference preference;
        private PaymentBody.Builder body;

        public static Builder createWhiteLabelLegacyPaymentRequest(final Headers headers, final PaymentRequestBody paymentRequestBody,
                                                                   final Preference preference, final String requestId){
            final Builder builder = new Builder(headers, paymentRequestBody.getToken(), requestId);
            builder.body = PaymentBody.Builder.createWhiteLabelLegacyBuilder(paymentRequestBody, preference);
            return builder;
        }

        public static Builder createWhiteLabelPaymentRequest(final Headers headers, final PaymentData paymentData,
                                                             final Preference preference, final String requestId) {

            final String token = paymentData.getToken() != null ? paymentData.getToken().getId() : null;
            final Builder builder = new Builder(headers, token, requestId);
            builder.body = PaymentBody.Builder.createWhiteLabelBuilder(paymentData, preference);
            return builder;
        }

        public static Builder createBlackLabelPaymentRequest(final Headers headers, final PaymentData paymentData,
                                                      final Preference preference, final String requestId){
            final String token = paymentData.getToken() != null ? paymentData.getToken().getId() : null;
            final Builder builder = new Builder(headers, token, requestId);
            builder.body = PaymentBody.Builder.createBlackLabelBuilder(paymentData, preference);
            return builder;
        }

        Builder (final Headers headers, final String token, final String requestId) {
            this.headers = HeadersUtils.completePaymentHeaders(headers, token, requestId);
        }

        public Builder withCallerId(final long callerId) {
            this.callerId = callerId;
            return this;
        }

        public Builder withClientId(final long clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withPreference(final Preference preference) {
            this.preference = preference;
            return this;
        }

        public Builder withCollector(final long collectorId) {
            body.withCollector(collectorId);
            return this;
        }

        public Builder withOrder(final Order order) {
            body.withOrder(order);
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

