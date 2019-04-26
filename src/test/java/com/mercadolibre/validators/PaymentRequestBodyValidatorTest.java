package com.mercadolibre.validators;

import com.mercadolibre.constants.PaymentsRequestBodyParams;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.payment.PaymentRequestBody;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.gson.GsonWrapper;
import org.apache.http.HttpStatus;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;

import static com.mercadolibre.constants.QueryParamsConstants.PAYMENT_METHOD_ID;
import static com.mercadolibre.constants.QueryParamsConstants.PUBLIC_KEY;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PaymentRequestBodyValidatorTest {

    private PaymentRequestBodyValidator validator = new PaymentRequestBodyValidator();

    @Test
    public void paymentRequestBodyValidator_validationSucceed() throws IOException, ValidationException {
        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyComplete.json")),
                PaymentRequestBody.class);
        validator.validate(paymentRequestBody);
    }

    @Test
    public void paymentRequestBodyValidator_withoutPublicKey_validationFail() throws IOException {
        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithoutPK.json")),
                PaymentRequestBody.class);
        try {
            validator.validate(paymentRequestBody);
        } catch (ValidationException e) {
            assertThat(e.getCode(), is("bad_request"));
            assertThat(e.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
            assertThat(e.getDescription(), is(String.format("%s is required.", PUBLIC_KEY)));
        }
    }

    @Test
    public void paymentRequestBodyValidator_withoutPaymentMethodId_validationFail() throws IOException {
        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithoutPM.json")),
                PaymentRequestBody.class);
        try {
            validator.validate(paymentRequestBody);
        } catch (ValidationException e) {
            assertThat(e.getDescription(), is(String.format("%s is required.", PAYMENT_METHOD_ID)));
        }
    }

    @Test
    public void paymentRequestBodyValidator_withoutPrefId_validationFail() throws IOException {
        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithoutPrefId.json")),
                PaymentRequestBody.class);
        try {
            validator.validate(paymentRequestBody);
        } catch (ValidationException e) {
            assertThat(e.getDescription(), is(String.format("%s is required.", PaymentsRequestBodyParams.PREF_ID)));
        }
    }

    @Test
    public void paymentRequestBodyValidator_withoutPayer_validationFail() throws IOException {
        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithoutPayer.json")),
                PaymentRequestBody.class);
        try {
            validator.validate(paymentRequestBody);
        } catch (ValidationException e) {
            assertThat(e.getDescription(), is(String.format("%s is required.", PaymentsRequestBodyParams.EMAIL)));
        }
    }

    @Test
    public void paymentRequestBodyValidator_withNegativeIssuerId_validationFail() throws IOException {
        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithoutNumberIssuerId.json")),
                PaymentRequestBody.class);
        try {
            validator.validate(paymentRequestBody);
        } catch (ValidationException e) {
            assertThat(e.getDescription(), is(String.format("%s must be number.", PaymentsRequestBodyParams.ISSUER_ID)));
        }
    }

    @Test
    public void paymentRequestBodyValidator_withNegativeInstallments_validationFail() throws IOException {
        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithNegativeInstallments.json")),
                PaymentRequestBody.class);
        try {
            validator.validate(paymentRequestBody);
        } catch (ValidationException e) {
            assertThat(e.getDescription(), is(String.format("%s must be positive.", PaymentsRequestBodyParams.INSTALLMENTS)));
        }
    }
}
