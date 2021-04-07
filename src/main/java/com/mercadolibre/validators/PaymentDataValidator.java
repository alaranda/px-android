package com.mercadolibre.validators;

import static com.mercadolibre.constants.QueryParamsConstants.PAYMENT_METHOD_ID;
import static java.lang.String.format;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.constants.PaymentsRequestBodyParams;
import com.mercadolibre.dto.payment.PayerCost;
import com.mercadolibre.dto.payment.PaymentData;
import com.mercadolibre.px.dto.lib.card.Issuer;
import com.mercadolibre.px.exceptions.ValidationException;
import spark.utils.StringUtils;

public class PaymentDataValidator {

  /**
   * Valida los parametros del PaymentDataBody
   *
   * @param paymentData objeto con el body del payment request
   * @throws ValidationException falla la validacion
   */
  public void validate(final PaymentData paymentData) throws ValidationException {
    SimpleValidator.from((body) -> body != null, format("%s is required.", "body"))
        .validate(paymentData)
        .throwIfInvalid();

    SimpleValidatorHelper.notNullString(PAYMENT_METHOD_ID)
        .validate(paymentData.getPaymentMethod().getId())
        .throwIfInvalid();
    validateOptionalPositiveParam(paymentData.getPayerCost());
    validateOptionalCard(paymentData.getIssuer());
  }

  private void validateOptionalCard(final Issuer issuer) throws ValidationException {
    if (issuer != null && !StringUtils.isBlank(issuer.getId().toString())) {
      SimpleValidatorHelper.isNumber(PaymentsRequestBodyParams.ISSUER_ID)
          .validate(issuer.getId().toString())
          .throwIfInvalid();
    }
  }

  private void validateOptionalPositiveParam(final PayerCost payerCost) throws ValidationException {
    if (payerCost != null && payerCost.getInstallments() != null) {
      SimpleValidatorHelper.isIntPositive(Constants.INSTALLMENTS)
          .validate(payerCost.getInstallments())
          .throwIfInvalid();
    }
  }
}
