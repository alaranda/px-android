package com.mercadolibre.endpoints;

import com.mercadolibre.api.MockPaymentAPI;
import com.mercadolibre.mocks.MockPreferenceAPI;
import com.mercadolibre.api.MockPublicKeyAPI;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class PaymentRouterTest {

    @Before
    public void before() {
        RequestMockHolder.clear();
    }

    public static final String PUBLIC_KEY_MLA_1 = "TEST-c8473389-df81-468c-96a8-71e2c7cd1f89";
    public static final String PREFERENCE_ID_1 = "138275050-21ff9440-f9ab-4467-8ad7-c2847c064014";
    public static final long CALLER_ID_MLA_1 = 204318018L;
    public static final long CLIENT_ID_MLA_1 = 7977122093299909L;

    @Test
    public void doPayment_withoutBody_400() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments");

        final Response response = post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void doPayment_invalidPK_404() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments");
        final String responseBody = IOUtils.toString(getClass().getResourceAsStream("/publicKey/invalidPK.json"));
        MockPublicKeyAPI.getPublicKey("1", HttpStatus.SC_NOT_FOUND,
                responseBody);

        final Response response = given()
                .body(IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithInvalidPK.json")))
                .with()
                .contentType("application/json").post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void doPayment_validPayment_200() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments");
        MockPublicKeyAPI.getPublicKey(PUBLIC_KEY_MLA_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/publicKey/TEST-c8473389-df81-468c-96a8-71e2c7cd1f89.json")));
        MockPreferenceAPI.getById(PREFERENCE_ID_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-21ff9440-f9ab-4467-8ad7-c2847c064014.json")));
        MockPaymentAPI.doPayment(CALLER_ID_MLA_1,
                CLIENT_ID_MLA_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/4141386674.json")));

        final Response response = given()
                .body(IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyComplete.json")))
                .with()
                .contentType("application/json").post(uriBuilder.build());

        Assert.assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertNotNull(response.getBody().print());

    }
}
