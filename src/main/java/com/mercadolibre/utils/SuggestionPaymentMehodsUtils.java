package com.mercadolibre.utils;

import static com.mercadolibre.constants.Constants.STATUS_APPROVED;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.ACCOUNT_MONEY;
import static com.mercadolibre.px.toolkit.constants.PaymentTypeId.CREDIT_CARD;
import static com.mercadolibre.px.toolkit.constants.PaymentTypeId.DEBIT_CARD;
import static com.mercadolibre.px.toolkit.constants.PaymentTypeId.DIGITAL_CURRENCY;

import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.Installment;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.PaymentMethodSelected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.tracking.TrackingData;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

public final class SuggestionPaymentMehodsUtils {

  private static final String SAME = "same";
  private static final String LESS = "less";
  private static final String MORE = "more";

  public PaymentMethodSelected findPaymentMethodSuggestionsAmount(
      final RemediesRequest remediesRequest) {

    final List<AlternativePayerPaymentMethod> alternativePayerPaymentMethodList =
        remediesRequest.getAlternativePayerPaymentMethods();

    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        remediesRequest.getPayerPaymentMethodRejected();

    if (CollectionUtils.isEmpty(alternativePayerPaymentMethodList)
        || null == payerPaymentMethodRejected) {
      return null;
    }

    for (AlternativePayerPaymentMethod alternativePayerPaymentMethod :
        remediesRequest.getAlternativePayerPaymentMethods()) {

      final String paymentTypeId = alternativePayerPaymentMethod.getPaymentTypeId();

      if (paymentTypeId.equalsIgnoreCase(ACCOUNT_MONEY)
          || paymentTypeId.equalsIgnoreCase(DIGITAL_CURRENCY.toString())) {

        return PaymentMethodSelected.builder()
            .alternativePayerPaymentMethod(alternativePayerPaymentMethod)
            .build();
      }

      if (paymentTypeId.equalsIgnoreCase(DEBIT_CARD.toString())) {

        final PaymentMethodSelected.PaymentMethodSelectedBuilder paymentMethodSelectedBuilder =
            PaymentMethodSelected.builder()
                .alternativePayerPaymentMethod(alternativePayerPaymentMethod);

        setEscRequired(
            paymentMethodSelectedBuilder,
            alternativePayerPaymentMethod.getEscStatus(),
            alternativePayerPaymentMethod.isEsc());

        return paymentMethodSelectedBuilder.build();
      }

      if (paymentTypeId.equalsIgnoreCase(CREDIT_CARD.toString())) {

        final Installment installmentSelected =
            findInstallmentSelected(alternativePayerPaymentMethod, payerPaymentMethodRejected);

        if (null != installmentSelected) {

          alternativePayerPaymentMethod.setInstallmentsList(Arrays.asList(installmentSelected));
          alternativePayerPaymentMethod.setInstallments(
              payerPaymentMethodRejected.getInstallments());

          final PaymentMethodSelected.PaymentMethodSelectedBuilder paymentMethodSelectedBuilder =
              PaymentMethodSelected.builder()
                  .alternativePayerPaymentMethod(alternativePayerPaymentMethod);

          setEscRequired(
              paymentMethodSelectedBuilder,
              alternativePayerPaymentMethod.getEscStatus(),
              alternativePayerPaymentMethod.isEsc());

          return paymentMethodSelectedBuilder.build();
        }
      }
    }

    return null;
  }

  private static Installment findInstallmentSelected(
      final AlternativePayerPaymentMethod alternativePayerPaymentMethod,
      final PayerPaymentMethodRejected payerPaymentMethodRejected) {

    for (Installment installment : alternativePayerPaymentMethod.getInstallmentsList()) {

      if (installment.getInstallments() == payerPaymentMethodRejected.getInstallments()
          && installment.getTotalAmount().compareTo(payerPaymentMethodRejected.getTotalAmount())
              == 0) {
        return installment;
      }
    }

    return null;
  }

  private static void setEscRequired(
      final PaymentMethodSelected.PaymentMethodSelectedBuilder paymentMethodSelectedBuilder,
      final String escStatus,
      final boolean esc) {
    if (!escStatus.equalsIgnoreCase(STATUS_APPROVED) || !esc) {
      paymentMethodSelectedBuilder.remedyCvvRequired(true);
    }
  }

  public TrackingData generateTrackingData(
      final PayerPaymentMethodRejected payerPaymentMethodRejected,
      final AlternativePayerPaymentMethod alternativePayerPaymentMethod,
      final boolean frictionless) {

    final TrackingData trackingData = new TrackingData();
    trackingData.setPaymentMethodId(alternativePayerPaymentMethod.getPaymentMethodId());
    trackingData.setPaymentTypeId(alternativePayerPaymentMethod.getPaymentTypeId());
    trackingData.setFrictionless(String.valueOf(frictionless));

    if (payerPaymentMethodRejected.getPaymentTypeId().equalsIgnoreCase(ACCOUNT_MONEY)) {

      trackingData.setInstallments(
          compareInstallments(
              1, alternativePayerPaymentMethod.getInstallmentsList().get(0).getInstallments()));

    } else if (alternativePayerPaymentMethod.getPaymentTypeId().equalsIgnoreCase(ACCOUNT_MONEY)) {

      trackingData.setInstallments(
          compareInstallments(payerPaymentMethodRejected.getInstallments(), 1));
    } else {

      final int installmentRejected = payerPaymentMethodRejected.getInstallments();
      final Installment installment = alternativePayerPaymentMethod.getInstallmentsList().get(0);
      trackingData.setInstallments(
          compareInstallments(installmentRejected, installment.getInstallments()));
      trackingData.setAmount(
          compareAmount(payerPaymentMethodRejected.getTotalAmount(), installment.getTotalAmount()));
    }

    return trackingData;
  }

  private String compareInstallments(
      final int installmentRejected, final int installmentSuggested) {

    if (installmentSuggested == installmentRejected) {
      return SAME;
    } else if (installmentSuggested > installmentRejected) {
      return MORE;
    }
    return LESS;
  }

  private String compareAmount(final BigDecimal amountRejected, final BigDecimal amountSuggested) {

    final int compare = amountRejected.compareTo(amountSuggested);
    if (compare == 0) {
      return SAME;
    } else if (compare == 1) {
      return LESS;
    }
    return MORE;
  }
}
