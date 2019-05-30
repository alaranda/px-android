package com.mercadolibre.endpoints;

import com.mercadolibre.api.MockAccessTokenAPI;
import com.mercadolibre.api.MockPaymentAPI;
import com.mercadolibre.api.MockPreferenceAPI;
import com.mercadolibre.api.MockPublicKeyAPI;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

public class PaymentRouterTest {

    @Before
    public void before() {
        RequestMockHolder.clear();
    }

    public static final String PUBLIC_KEY_MLA_1 = "TEST-c8473389-df81-468c-96a8-71e2c7cd1f89";
    public static final String PREFERENCE_ID_1 = "138275050-21ff9440-f9ab-4467-8ad7-c2847c064014";
    public static final long CALLER_ID_MLA_1 = 204318018L;
    public static final long CLIENT_ID_MLA_1 = 7977122093299909L;
    public static final long CLIENT_ID_MLA = 889238428771302L;
    public static final long CALLER_ID_MLA = 243962506L;
    public static final String ACCES_TOKEN = "APP_USR-4190463107814393-052112-e3abec7009c820171d714ad739f2b669-395662610";

    @Test
    public void doPayment_withoutBody_400() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments");

        final Response response = post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void doPayment_invalidPK_404() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments")
                .addParameter("public_key", "1");;
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
    public void doPayment_validWhiteLabelPayment_200() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments")
                .addParameter("public_key", PUBLIC_KEY_MLA_1);;
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

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getBody().print(), is(not(nullValue())));

    }

    @Test
    public void doPayment_invalidBlackLabelPayment_400() throws URISyntaxException, IOException {
        final URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments")
                .addParameter("acces_token", ACCES_TOKEN)
                .addParameter("public_key", PUBLIC_KEY_MLA_1);
        MockPublicKeyAPI.getPublicKey(PUBLIC_KEY_MLA_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/publicKey/TEST-c8473389-df81-468c-96a8-71e2c7cd1f89.json")));
        MockPreferenceAPI.getById(PREFERENCE_ID_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-21ff9440-f9ab-4467-8ad7-c2847c064014.json")));
        MockPaymentAPI.doPayment(CLIENT_ID_MLA,
                CALLER_ID_MLA, HttpStatus.SC_BAD_REQUEST,
                IOUtils.toString(getClass().getResourceAsStream("/payment/400_invalidUser.json")));
        MockAccessTokenAPI.getAccessToken(ACCES_TOKEN, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/accesToken/APP_USR-4190463107814393-052112-e3abec7009c820171d714ad739f2b669-395662610.json")));

        final Response response = given()
                .body(IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithAccountMoney.json")))
                .with()
                .contentType("application/json").post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBody().print(), is(not(nullValue())));
    }
}
