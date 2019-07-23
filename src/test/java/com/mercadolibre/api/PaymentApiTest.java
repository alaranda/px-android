package com.mercadolibre.api;

import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentBody;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.utils.Either;
import org.apache.http.HttpStatus;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PaymentApiTest {

    private final long CALLER_ID_MLA = 243962506L;
    private final long CLIENT_ID_MLA = 889238428771302L;
    private final Context context = new Context.Builder( UUID.randomUUID().toString()).build();
    private final PaymentAPI service = PaymentAPI.INSTANCE;

    @Test
    public void doPayment_bodyOk_approved() throws IOException, ApiException {
        MockPaymentAPI.doPayment(CALLER_ID_MLA,
                CLIENT_ID_MLA, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/4141386674.json")));

        final PaymentBody body = GsonWrapper.
                fromJson(IOUtils.toString(getClass().getResourceAsStream("/paymentBody/bodyOk.json")),
                        PaymentBody.class);

        final Either<Payment, ApiError> paymentResponse = service.doPayment(context, CALLER_ID_MLA, CLIENT_ID_MLA, body, new Headers());
        final Payment payment = paymentResponse.getValue();

        assertThat(payment.getStatus(), is("approved"));
        assertThat(payment.getStatusDetail(), is("accredited"));
    }

    @Test
    public void doPayment_bodyError_400() throws IOException, RestException {
        try {
            MockPaymentAPI.doPayment(CALLER_ID_MLA,
                    CLIENT_ID_MLA, HttpStatus.SC_NOT_FOUND,
                    IOUtils.toString(getClass().getResourceAsStream("/payment/status400.json")));
            final PaymentBody body = GsonWrapper.
                    fromJson(IOUtils.toString(getClass().getResourceAsStream("/paymentBody/bodyOk.json")),
                            PaymentBody.class);
            service.doPayment(context, CALLER_ID_MLA, CLIENT_ID_MLA, body, new Headers());
        } catch (ApiException e) {
            assertThat(e.getCode(), is("bad_request"));
            assertThat(e.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        }
    }

}
