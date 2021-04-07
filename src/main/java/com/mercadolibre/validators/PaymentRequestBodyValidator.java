package com.mercadolibre.validators;

import static com.mercadolibre.constants.QueryParamsConstants.PAYMENT_METHOD_ID;
import static java.lang.String.format;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.constants.PaymentsRequestBodyParams;
import com.mercadolibre.dto.payment.PaymentRequestBody;
import com.mercadolibre.px.exceptions.ValidationException;
import spark.utils.StringUtils;

public class PaymentRequestBodyValidator {

  /**
   * Valida los parametros del PaymentRequestBody
   *
   * @param paymentRequestBody objeto con el body del payment request
   * @throws ValidationException falla la validacion
   */
  public void validate(final PaymentRequestBody paymentRequestBody) throws ValidationException {
    SimpleValidator.from((body) -> body != null, format("%s is required.", "body"))
        .validate(paymentRequestBody)
        .throwIfInvalid();
    SimpleValidatorHelper.notNullString(PAYMENT_METHOD_ID)
        .validate(paymentRequestBody.getPaymentMethodId())
        .throwIfInvalid();
    SimpleValidatorHelper.notNullString(PaymentsRequestBodyParams.PREF_ID)
        .validate(paymentRequestBody.getPrefId())
        .throwIfInvalid();
    validateEmail(paymentRequestBody);
    validateOptionalPositiveParam(paymentRequestBody.getInstallments());
    validateOptionalPositiveParam(paymentRequestBody.getIssuerId());
  }

  private void validateOptionalPositiveParam(final String issuerId) throws ValidationException {
    if (!StringUtils.isBlank(issuerId)) {
      SimpleValidatorHelper.isNumber(PaymentsRequestBodyParams.ISSUER_ID)
          .validate(issuerId)
          .throwIfInvalid();
    }
  }

  private void validateOptionalPositiveParam(final Integer installments)
      throws ValidationException {
    if (installments != null) {
      SimpleValidatorHelper.isIntPositive(Constants.INSTALLMENTS)
          .validate(installments)
          .throwIfInvalid();
    }
  }

  private void validateEmail(final PaymentRequestBody paymentRequestBody)
      throws ValidationException {
    SimpleValidator.from(
            (payer) -> payer != null, format("%s is required.", PaymentsRequestBodyParams.EMAIL))
        .validate(paymentRequestBody.getPayer())
        .throwIfInvalid();
    SimpleValidatorHelper.notNullString(PaymentsRequestBodyParams.EMAIL)
        .validate(paymentRequestBody.getPayer().getEmail())
        .throwIfInvalid();
  }
}
