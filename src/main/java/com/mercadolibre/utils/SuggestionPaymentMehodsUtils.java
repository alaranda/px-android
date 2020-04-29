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
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

public final class SuggestionPaymentMehodsUtils {

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
}
