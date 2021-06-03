package com.mercadolibre.dto.payment;

import com.mercadolibre.dto.Order;
import com.mercadolibre.px.dto.lib.preference.PointOfInteraction;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.utils.HeadersUtils;
import lombok.Getter;

/** Objeto con toda la informacion necesaria para hacer el request a payments */
@Getter
public class PaymentRequest {

  private Headers headers;
  private Long callerId;
  private Long clientId;
  private Preference preference;
  private PaymentBody body;

  private PaymentRequest(final Builder builder) {
    this.headers = builder.headers;
    this.callerId = builder.callerId;
    this.clientId = builder.clientId;
    this.preference = builder.preference;
    this.body = builder.body.build();
  }

  public static final class Builder {

    private Headers headers;
    private Long callerId;
    private Long clientId;
    private Preference preference;
    private PaymentBody.Builder body;

    public static Builder createWhiteLabelLegacyPaymentRequest(
        final Headers headers,
        final PaymentRequestBody paymentRequestBody,
        final Preference preference,
        final String requestId) {
      final Builder builder = new Builder(headers, paymentRequestBody.getToken(), requestId);
      builder.body =
          PaymentBody.Builder.createWhiteLabelLegacyBuilder(paymentRequestBody, preference);
      return builder;
    }

    public static Builder createWhiteLabelPaymentRequest(
        final Headers headers,
        final PaymentData paymentData,
        final Preference preference,
        final String requestId,
        final String validationProgramId,
        final PointOfInteraction pointOfInteraction) {

      final String token = paymentData.getToken() != null ? paymentData.getToken().getId() : null;
      final Builder builder = new Builder(headers, token, requestId);
      builder.body =
          PaymentBody.Builder.createWhiteLabelBuilder(paymentData, preference)
              .withValidationProgramId(validationProgramId)
              .withPointOfInteraction(pointOfInteraction);
      return builder;
    }

    public static Builder createBlackLabelPaymentRequest(
        final Headers headers,
        final PaymentData paymentData,
        final Preference preference,
        final String requestId,
        final String validationProgramId,
        final PointOfInteraction pointOfInteraction) {
      final String token = paymentData.getToken() != null ? paymentData.getToken().getId() : null;
      final Builder builder = new Builder(headers, token, requestId);
      builder.body =
          PaymentBody.Builder.createBlackLabelBuilder(paymentData, preference)
              .withValidationProgramId(validationProgramId)
              .withPointOfInteraction(pointOfInteraction);
      return builder;
    }

    Builder(final Headers headers, final String token, final String requestId) {
      this.headers = HeadersUtils.completePaymentHeaders(headers, token, requestId);
    }

    public Builder withCallerId(final Long callerId) {
      this.callerId = callerId;
      return this;
    }

    public Builder withClientId(final Long clientId) {
      this.clientId = clientId;
      return this;
    }

    public Builder withPreference(final Preference preference) {
      this.preference = preference;
      return this;
    }

    public Builder withCollector(final Long collectorId, final Long operatorIdCollector) {
      body.withCollector(collectorId, operatorIdCollector);
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
