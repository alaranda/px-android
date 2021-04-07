package com.mercadolibre.service;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDY_INVALID_PAYMENT_ID;
import static com.mercadolibre.utils.Translations.REMEDY_OTHER_REASON_MESSAGE;
import static com.mercadolibre.utils.Translations.REMEDY_OTHER_REASON_TITLE;

import com.mercadolibre.api.PaymentAPI;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.px.dto.ApiError;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.service.remedy.RemedyCvv;
import com.mercadolibre.service.remedy.RemedyInterface;
import com.mercadolibre.service.remedy.RemedySuggestionPaymentMethod;
import com.mercadolibre.service.remedy.RemedyTypes;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class RemediesService {

  private final RemedyTypes remedyTypes;
  private final RemedySuggestionPaymentMethod remedySuggestionPaymentMethod;

  private final Pattern pattern = Pattern.compile("\\d+$");

  public RemediesService() {
    this.remedyTypes = new RemedyTypes();
    this.remedySuggestionPaymentMethod =
        new RemedySuggestionPaymentMethod(
            new RemedyCvv(), REMEDY_OTHER_REASON_TITLE, REMEDY_OTHER_REASON_MESSAGE);
  }

  /**
   * A partir del "status_detail" del payment, decide el remedy a aplicar (si es que se puede).
   *
   * @param context context
   * @param paymentId payment id
   * @param remediesRequest remediesRequest
   * @throws ApiException api exception
   * @return RemediesResponse
   */
  public RemediesResponse getRemedy(
      final Context context, final String paymentId, final RemediesRequest remediesRequest)
      throws ApiException {

    final PaymentAPI paymentAPI = PaymentAPI.INSTANCE;

    Optional<Payment> paymentOptional = Optional.empty();

    if (pattern.matcher(paymentId).matches()) {
      final CompletableFuture<Either<Payment, ApiError>> paymentFuture =
          paymentAPI.getAsyncPayment(context, paymentId);

      paymentOptional = paymentAPI.getPaymentFromFuture(context, paymentFuture);
    }

    if (!paymentOptional.isPresent()) {
      DatadogRemediesMetrics.trackRemediesInfo(REMEDY_INVALID_PAYMENT_ID, context, remediesRequest);
      return remedySuggestionPaymentMethod.applyRemedy(
          context, remediesRequest, new RemediesResponse());
    }

    final Payment payment = paymentOptional.get();
    remediesRequest.setRiskExcecutionId(payment.getRiskExecutionId());
    remediesRequest.setStatusDetail(payment.getStatusDetail());

    final List<RemedyInterface> remediesInterface =
        remedyTypes.getRemedyByType(payment.getStatusDetail());

    final RemediesResponse remediesResponse = new RemediesResponse();

    remediesInterface.forEach(
        remedyInterface -> remedyInterface.applyRemedy(context, remediesRequest, remediesResponse));

    return remediesResponse;
  }
}
