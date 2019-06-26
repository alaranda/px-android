package com.mercadolibre.endpoints;

import com.mercadolibre.api.*;
import com.mercadolibre.constants.Constants;
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
    public void doPayment_whiteLabelWithoutBody_400() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/legacy_payments");

        final Response response = post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void legacyPayments_invalidPK_400() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/legacy_payments");

        final String responseBody = IOUtils.toString(getClass().getResourceAsStream("/publicKey/invalidPK.json"));
        MockPublicKeyAPI.getPublicKey("test-error", HttpStatus.SC_BAD_REQUEST,
                responseBody);

        final Response response = given()
                .body(IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithInvalidPK.json")))
                .with()
                .contentType("application/json").post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void legacyPayments_success_200() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/legacy_payments");
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
    public void payments_withoutBody_400() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments");

        final Response response = post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void payments_success_200() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments")
                .addParameter(Constants.ACCESS_TOKEN, ACCES_TOKEN)
                .addParameter(Constants.PUBLIC_KEY, PUBLIC_KEY_MLA_1);
        MockPublicKeyAPI.getPublicKey(PUBLIC_KEY_MLA_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/publicKey/TEST-c8473389-df81-468c-96a8-71e2c7cd1f89.json")));
        MockAccessTokenAPI.getAccessToken(ACCES_TOKEN, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/accesToken/APP_USR-4190463107814393-052112-e3abec7009c820171d714ad739f2b669-395662610.json")));
        MockPreferenceAPI.getById(PREFERENCE_ID_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-21ff9440-f9ab-4467-8ad7-c2847c064014.json")));
        MockPaymentAPI.doPayment(CALLER_ID_MLA_1,
                CLIENT_ID_MLA_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/payment/4141386674.json")));
        MockMerchantOrderApi.createMerchantOrder("395662610", HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/merchantOrders/merchantOrderResponse.json")));

        final Response response = given()
                .body(IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyCompleteWithoutPK.json")))
                .with()
                .contentType("application/json").post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getBody().print(), is(not(nullValue())));

    }

    @Test
    public void payments_invalidUsers_400() throws URISyntaxException, IOException {
        final URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments")
                .addParameter(Constants.ACCESS_TOKEN, ACCES_TOKEN)
                .addParameter(Constants.PUBLIC_KEY, PUBLIC_KEY_MLA_1);
        MockPublicKeyAPI.getPublicKey(PUBLIC_KEY_MLA_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/publicKey/TEST-c8473389-df81-468c-96a8-71e2c7cd1f89.json")));
        MockPreferenceAPI.getById(PREFERENCE_ID_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-21ff9440-f9ab-4467-8ad7-c2847c064014.json")));
        MockPaymentAPI.doPayment(CLIENT_ID_MLA,
                CALLER_ID_MLA, HttpStatus.SC_BAD_REQUEST,
                IOUtils.toString(getClass().getResourceAsStream("/payment/400_invalidUser.json")));
        MockAccessTokenAPI.getAccessToken(ACCES_TOKEN, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/accesToken/APP_USR-4190463107814393-052112-e3abec7009c820171d714ad739f2b669-395662610.json")));
        MockMerchantOrderApi.createMerchantOrder("395662610", HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/merchantOrders/merchantOrderResponse.json")));

        final Response response = given()
                .body(IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithAccountMoney.json")))
                .with()
                .contentType("application/json").post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBody().print(), is(not(nullValue())));
    }

    @Test
    public void payments_unsupportPaymentMethods_400() throws URISyntaxException, IOException {
        final URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments")
                .addParameter(Constants.ACCESS_TOKEN, ACCES_TOKEN)
                .addParameter(Constants.PUBLIC_KEY, PUBLIC_KEY_MLA_1);

        final Response response = given()
                .body(IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithTwoPaymentMethods.json")))
                .with()
                .contentType("application/json").post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBody().print(), is(not(nullValue())));
    }

    @Test
    public void payments_withoutPK_400() throws URISyntaxException, IOException {
        final URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments")
                .addParameter(Constants.ACCESS_TOKEN, ACCES_TOKEN);

        final Response response = given()
                .body(IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithAccountMoney.json")))
                .with()
                .contentType("application/json").post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBody().print(), is(not(nullValue())));
    }

    @Test
    public void payments_invalidAT_400() throws URISyntaxException, IOException {
        final URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments")
                .addParameter(Constants.ACCESS_TOKEN, ACCES_TOKEN)
                .addParameter(Constants.PUBLIC_KEY, PUBLIC_KEY_MLA_1);

        MockPublicKeyAPI.getPublicKey(PUBLIC_KEY_MLA_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/publicKey/TEST-c8473389-df81-468c-96a8-71e2c7cd1f89.json")));
        MockAccessTokenAPI.getAccessToken(ACCES_TOKEN, HttpStatus.SC_BAD_REQUEST,
                IOUtils.toString(getClass().getResourceAsStream("/accesToken/invalidAT.json")));
        MockPreferenceAPI.getById(PREFERENCE_ID_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-21ff9440-f9ab-4467-8ad7-c2847c064014.json")));

        final Response response = given()
                .body(IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithAccountMoney.json")))
                .with()
                .contentType("application/json").post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        assertThat(response.getBody().print(), is(not(nullValue())));
    }

    @Test
    public void payments_invalidPref_400() throws URISyntaxException, IOException {
        final URIBuilder uriBuilder = new URIBuilder("/px_mobile/payments")
                .addParameter(Constants.ACCESS_TOKEN, ACCES_TOKEN)
                .addParameter(Constants.PUBLIC_KEY, PUBLIC_KEY_MLA_1);

        MockPublicKeyAPI.getPublicKey(PUBLIC_KEY_MLA_1, HttpStatus.SC_OK,
                IOUtils.toString(getClass().getResourceAsStream("/publicKey/TEST-c8473389-df81-468c-96a8-71e2c7cd1f89.json")));
        MockPreferenceAPI.getById(PREFERENCE_ID_1, HttpStatus.SC_BAD_REQUEST,
                IOUtils.toString(getClass().getResourceAsStream("/preference/preferenceNotFound.json")));

        final Response response = given()
                .body(IOUtils.toString(getClass().getResourceAsStream("/paymentRequestBody/bodyWithAccountMoney.json")))
                .with()
                .contentType("application/json").post(uriBuilder.build());

        assertThat(response.getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
        assertThat(response.getBody().print(), is(not(nullValue())));
    }

}
