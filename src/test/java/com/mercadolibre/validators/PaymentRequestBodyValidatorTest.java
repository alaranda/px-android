package com.mercadolibre.validators;

import com.google.gson.reflect.TypeToken;
import com.mercadolibre.constants.PaymentsRequestBodyParams;
import com.mercadolibre.dto.payment.PaymentRequestBody;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.gson.GsonWrapper;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.mercadolibre.constants.QueryParamsConstants.PAYMENT_METHOD_ID;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PaymentRequestBodyValidatorTest {

    private PaymentRequestBodyValidator validator = new PaymentRequestBodyValidator();

    @Test
    public void paymentRequestBodyValidator_validationSucceed() throws IOException, ValidationException {
        final Type bodyListType = new TypeToken<ArrayList<PaymentRequestBody>>(){}.getType();
        final List<PaymentRequestBody> paymentRequestBodyList = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyComplete.json")),
                bodyListType);
        validator.validate(paymentRequestBodyList.get(0));
    }

    @Test
    public void paymentRequestBodyValidator_withoutPaymentMethodId_validationFail() throws IOException {
        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithoutPM.json")),
                PaymentRequestBody.class);
        try {
            validator.validate(paymentRequestBody);
        } catch (ValidationException e) {
            assertThat(e.getMessage(), is(String.format("%s is required.", PAYMENT_METHOD_ID)));
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
            assertThat(e.getMessage(), is(String.format("%s is required.", PaymentsRequestBodyParams.PREF_ID)));
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
            assertThat(e.getMessage(), is(String.format("%s is required.", PaymentsRequestBodyParams.EMAIL)));
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
            assertThat(e.getMessage(), is(String.format("%s must be number.", PaymentsRequestBodyParams.ISSUER_ID)));
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
            assertThat(e.getMessage(), is(String.format("%s must be positive.", PaymentsRequestBodyParams.INSTALLMENTS)));
        }
    }

    @Test
    public void paymentRequestBodyValidator_withOutInstallmetns_validationSucceed() throws IOException, ValidationException  {
        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyRapipago.json")),
                PaymentRequestBody.class);
        validator.validate(paymentRequestBody);
    }
}
