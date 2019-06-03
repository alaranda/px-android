package com.mercadolibre.service;

import com.mercadolibre.api.PaymentAPI;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentRequest;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.metrics.datadog.DatadogFuryMetricCollector;
import com.mercadolibre.utils.Either;

import java.util.LinkedList;
import java.util.List;

public enum PaymentService {

    INSTANCE;

    public Payment doPayment(final PaymentRequest paymentRequest) throws ApiException {
        Either<Payment, ApiError> payment = PaymentAPI.INSTANCE.doPayment(paymentRequest.getCallerId(), paymentRequest.getClientId(),
                paymentRequest.getBody(), paymentRequest.getHeaders());
        if (!payment.isValuePresent()) {
            throw new ApiException(payment.getAlternative());
        }
        DatadogFuryMetricCollector.INSTANCE.incrementCounter("px.checkout_mobile_payments.payment", getMetricTagsPayments(payment.getValue()));
        return payment.getValue();
    }

    private String[] getMetricTagsPayments(final Payment payment) {
        final List<String> tags = new LinkedList<>();
        tags.add("site_id:" + payment.getSiteId());
        tags.add("status_detail:" + payment.getStatusDetail());
        tags.add("payment_method_id:" + payment.getPaymentMethodId());
        tags.add("marketplace:" + payment.getMarketplace());

        return tags.toArray(new String[0]);
    }
}