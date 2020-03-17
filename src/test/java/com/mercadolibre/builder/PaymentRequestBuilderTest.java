package com.mercadolibre.builder;

import com.mercadolibre.dto.payment.PaymentRequest;
import com.mercadolibre.dto.payment.PaymentRequestBody;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.restclient.http.Headers;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PaymentRequestBuilderTest {


    @Test
    public void paymentBodyBuilder_preferencePayerData_bodyName() throws IOException {

        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyComplete.json")),
                PaymentRequestBody.class);

        final Preference preference = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/preference/preferenceWithOnlyPayer.json")),
                Preference.class);

        final PaymentRequest paymentRequest = PaymentRequest.Builder.createWhiteLabelLegacyPaymentRequest(new Headers(), paymentRequestBody, preference, "")
                .build();

        assertEquals(paymentRequest.getBody().getPayer().getFirstName(), "name preference");
        assertEquals(paymentRequest.getBody().getPayer().getLastName(), "surname preference");
    }

    @Test
    public void paymentBodyBuilder_paymentRequestData_bodyName() throws IOException {

        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyRapipago.json")),
                PaymentRequestBody.class);

        final Preference preference = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-21ff9440-f9ab-4467-8ad7-c2847c064014.json")),
                Preference.class);

        final PaymentRequest paymentRequest = PaymentRequest.Builder.createWhiteLabelLegacyPaymentRequest(new Headers(), paymentRequestBody, preference, "")
                .build();

        assertEquals(paymentRequest.getBody().getPayer().getFirstName(), "user test");
        assertEquals(paymentRequest.getBody().getPayer().getLastName(), "user test lasname");
    }

    @Test
    public void paymentBodyBuilder_paymentRequestCNPJ_bodyName() throws IOException {

        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyBoletoCNPJ.json")),
                PaymentRequestBody.class);

        final Preference preference = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-21ff9440-f9ab-4467-8ad7-c2847c064014.json")),
                Preference.class);

        final PaymentRequest paymentRequest = PaymentRequest.Builder.createWhiteLabelLegacyPaymentRequest(new Headers(), paymentRequestBody, preference, "")
                .build();

        assertEquals(paymentRequest.getBody().getPayer().getFirstName(), "MELI");
        assertEquals(paymentRequest.getBody().getPayer().getLastName(), "");
    }
}
