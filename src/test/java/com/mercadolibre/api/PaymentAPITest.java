package com.mercadolibre.api;

import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentBody;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.http.Headers;
import org.apache.http.HttpStatus;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.UUID;

import static com.mercadolibre.constants.Constants.API_CALL_PAYMENTS_FAILED;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class PaymentAPITest extends RestClientTestBase {

    private final Long CALLER_ID_MLA = 243962506L;
    private final Long CLIENT_ID_MLA = 889238428771302L;
    private final Context context = Context.builder().requestId(UUID.randomUUID().toString()).build();
    private final PaymentAPI paymentAPI = PaymentAPI.INSTANCE;

    @Test
    public void doPayment_bodyOk_approved() throws IOException, ApiException {
        MockPaymentAPI.doPayment(CALLER_ID_MLA,
                CLIENT_ID_MLA, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/4141386674.json")));

        final PaymentBody body = GsonWrapper
                .fromJson(IOUtils.toString(getClass().getResourceAsStream("/paymentBody/bodyOk.json")),
                        PaymentBody.class);

        final Either<Payment, ApiError> paymentResponse = paymentAPI.doPayment(context, CALLER_ID_MLA, CLIENT_ID_MLA, body, new Headers());
        final Payment payment = paymentResponse.getValue();

        assertThat(payment.getStatus(), is("approved"));
        assertThat(payment.getStatusDetail(), is("accredited"));
    }

    @Test
    public void doPayment_bodyError_400() throws IOException, ApiException {
        MockPaymentAPI.doPayment(CALLER_ID_MLA,
                CLIENT_ID_MLA, HttpStatus.SC_NOT_FOUND,
                IOUtils.toString(getClass().getResourceAsStream("/payment/status400.json")));
        final PaymentBody body = GsonWrapper
                .fromJson(IOUtils.toString(getClass().getResourceAsStream("/paymentBody/bodyOk.json")),
                        PaymentBody.class);
        final Either<Payment, ApiError> paymentResponse = paymentAPI.doPayment(context, CALLER_ID_MLA, CLIENT_ID_MLA, body, new Headers());
        assertFalse(paymentResponse.isValuePresent());
        assertThat(paymentResponse.getAlternative(), notNullValue());
    }

    @Test
    public void doPayment_timeout_throwsException() throws IOException {
        try {
            MockPaymentAPI.doPaymentFail(12345L,
                    111L);
            final PaymentBody body = GsonWrapper
                    .fromJson(IOUtils.toString(getClass().getResourceAsStream("/paymentBody/bodyOk.json")),
                            PaymentBody.class);
            paymentAPI.doPayment(context, CALLER_ID_MLA, CLIENT_ID_MLA, body, new Headers());
            fail("should fail");
        } catch (ApiException e) {
            assertEquals(e.getDescription(), API_CALL_PAYMENTS_FAILED);
            assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_GATEWAY);
            assertEquals(e.getDescription(), API_CALL_PAYMENTS_FAILED);
        }
    }

}
