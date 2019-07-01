package com.mercadolibre.validators;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.constants.PaymentsRequestBodyParams;
import com.mercadolibre.dto.Payer;
import com.mercadolibre.dto.payment.Issuer;
import com.mercadolibre.dto.payment.PayerCost;
import com.mercadolibre.dto.payment.PaymentData;
import com.mercadolibre.exceptions.ValidationException;
import spark.utils.StringUtils;

import java.util.List;

import static com.mercadolibre.constants.QueryParamsConstants.PAYMENT_METHOD_ID;
import static java.lang.String.format;

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
        //validateEmail(paymentData.getPayer());
        validateOptionalPositiveParam(paymentData.getPayerCost());
        validateOptionalCard(paymentData.getIssuer());
    }

    private void validateOptionalCard(final Issuer issuer) throws ValidationException {
        if (issuer != null && !StringUtils.isBlank(issuer.getId().toString())) {
            SimpleValidatorHelper.isNumber(PaymentsRequestBodyParams.ISSUER_ID).validate(issuer.getId().toString()).throwIfInvalid();
        }
    }

    private void validateOptionalPositiveParam(final PayerCost payerCost) throws ValidationException {
        if (payerCost != null && payerCost.getInstallments() != null) {
            SimpleValidatorHelper.isIntPositive(Constants.INSTALLMENTS).validate(payerCost.getInstallments()).throwIfInvalid();
        }
    }

    private void validateEmail(final PaymentData paymentData) throws ValidationException {
        SimpleValidator.from((payer) -> payer != null, format("%s is required.", PaymentsRequestBodyParams.EMAIL))
                .validate(paymentData.getPayer())
                .throwIfInvalid();
        SimpleValidatorHelper.notNullString(PaymentsRequestBodyParams.EMAIL)
                .validate(paymentData.getPayer().getEmail())
                .throwIfInvalid();
    }
}
