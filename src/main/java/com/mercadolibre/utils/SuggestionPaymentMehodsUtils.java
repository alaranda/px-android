package com.mercadolibre.utils;

import static com.mercadolibre.constants.Constants.STATUS_APPROVED;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.ACCOUNT_MONEY;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.CREDIT_CARD;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.DEBIT_CARD;

import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.Installment;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.PaymentMethodSelected;
import com.mercadolibre.dto.tracking.TrackingData;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import spark.utils.CollectionUtils;

public final class SuggestionPaymentMehodsUtils {

  private static final String SAME = "same";
  private static final String LESS = "less";
  private static final String MORE = "more";

  public static PaymentMethodSelected getPaymentMethodSelected(
      final List<AlternativePayerPaymentMethod> paymentMethodsOrdered) {
    Iterator<AlternativePayerPaymentMethod> it = paymentMethodsOrdered.iterator();

    if (!it.hasNext()) {
      return null;
    }

    final AlternativePayerPaymentMethod alternativePayerPaymentMethod = it.next();

    List<Installment> installments = new ArrayList<>();
    final List<Installment> alternativeInstallments =
        alternativePayerPaymentMethod.getInstallmentsList();
    if (!CollectionUtils.isEmpty(alternativeInstallments)) {
      Iterator<Installment> itInstallments = alternativeInstallments.iterator();
      installments = Collections.singletonList(itInstallments.next());
    }

    final AlternativePayerPaymentMethod selectPaymentMethod =
        AlternativePayerPaymentMethod.builder()
            .customOptionId(alternativePayerPaymentMethod.getCustomOptionId())
            .paymentMethodId(alternativePayerPaymentMethod.getPaymentMethodId())
            .paymentTypeId(alternativePayerPaymentMethod.getPaymentTypeId())
            .escStatus(alternativePayerPaymentMethod.getEscStatus())
            .issuerName(alternativePayerPaymentMethod.getIssuerName())
            .lastFourDigit(alternativePayerPaymentMethod.getLastFourDigit())
            .securityCodeLength(alternativePayerPaymentMethod.getSecurityCodeLength())
            .securityCodeLocation(alternativePayerPaymentMethod.getSecurityCodeLocation())
            .installments(alternativePayerPaymentMethod.getInstallments())
            .installmentsList(installments)
            .bin(alternativePayerPaymentMethod.getBin())
            .build();

    final PaymentMethodSelected.PaymentMethodSelectedBuilder paymentMethodSelectedBuilder =
        PaymentMethodSelected.builder().alternativePayerPaymentMethod(selectPaymentMethod);

    setEscRequired(
        paymentMethodSelectedBuilder,
        alternativePayerPaymentMethod.getEscStatus(),
        alternativePayerPaymentMethod.getPaymentTypeId());

    return paymentMethodSelectedBuilder.build();
  }

  private static void setEscRequired(
      final PaymentMethodSelected.PaymentMethodSelectedBuilder paymentMethodSelectedBuilder,
      final String escStatus,
      final String paymentTypeId) {
    if (paymentTypeId.equalsIgnoreCase(CREDIT_CARD) || paymentTypeId.equalsIgnoreCase(DEBIT_CARD)) {
      if (!escStatus.equalsIgnoreCase(STATUS_APPROVED)) {
        paymentMethodSelectedBuilder.remedyCvvRequired(true);
      }
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
    trackingData.setCardSize(alternativePayerPaymentMethod.getCardSize().name().toLowerCase());

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
