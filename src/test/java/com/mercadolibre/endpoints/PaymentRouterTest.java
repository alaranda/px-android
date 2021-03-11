package com.mercadolibre.endpoints;

import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CLIENT_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.PUBLIC_KEY;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import com.mercadolibre.api.MockMerchantOrderAPI;
import com.mercadolibre.api.MockPaymentAPI;
import com.mercadolibre.api.MockPreferenceAPI;
import com.mercadolibre.api.MockPublicKeyAPI;
import com.mercadolibre.api.MockTedAPI;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import io.restassured.response.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

public class PaymentRouterTest {

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  public static final String PUBLIC_KEY_MLA_1 = "TEST-c8473389-df81-468c-96a8-71e2c7cd1f89";
  public static final String PREFERENCE_ID_1 = "138275050-21ff9440-f9ab-4467-8ad7-c2847c064014";
  public static final String PUBLIC_KEY_BLACKLABEL_AM = "TEST-d783da36-74a2-4378-85d1-76f498ca92c4";
  public static final String PREFERENCE_ID_BLACKLABEL_AM =
      "384414502-d095679d-f7d9-4653-ad71-4fb5feda3494";
  public static final Long CALLER_ID_MLA_1 = 204318018L;
  public static final Long CLIENT_ID_MLA_1 = 7977122093299909L;
  public static final Long CLIENT_ID_MLA = 889238428771302L;
  public static final Long CALLER_ID_MLA = 243962506L;

  @Test
  public void legacyPayments_whiteLabelWithoutBody_400() throws URISyntaxException {
    URIBuilder uriBuilder = new URIBuilder("/px_mobile/legacy_payments");

    final Response response = post(uriBuilder.build());

    assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
  }

  @Test
  public void legacyPayments_withoutPK_400() throws URISyntaxException, IOException {
    URIBuilder uriBuilder = new URIBuilder("/px_mobile/legacy_payments");

    final String responseBody =
        IOUtils.toString(getClass().getResourceAsStream("/publicKey/invalidPK.json"));
    MockPublicKeyAPI.getPublicKey("test-error", HttpStatus.SC_BAD_REQUEST, responseBody);

    final Response response =
        given()
            .body(
                IOUtils.toString(
                    getClass().getResourceAsStream("/paymentRequestBody/bodyWithoutPK.json")))
            .with()
            .contentType("application/json")
            .post(uriBuilder.build());

    assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
  }

  @Test
  public void legacyPayments_invalidPK_400() throws URISyntaxException, IOException {
    URIBuilder uriBuilder = new URIBuilder("/px_mobile/legacy_payments");

    final String responseBody =
        IOUtils.toString(getClass().getResourceAsStream("/publicKey/invalidPK.json"));
    MockPublicKeyAPI.getPublicKey("test-error", HttpStatus.SC_BAD_REQUEST, responseBody);

    final Response response =
        given()
            .body(
                IOUtils.toString(
                    getClass().getResourceAsStream("/paymentRequestBody/bodyWithInvalidPK.json")))
            .with()
            .contentType("application/json")
            .post(uriBuilder.build());

    assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
  }

  @Test
  public void legacyPayments_success_200() throws URISyntaxException, IOException {
    URIBuilder uriBuilder = new URIBuilder("/px_mobile/legacy_payments");
    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_MLA_1,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
    MockPreferenceAPI.getById(
        PREFERENCE_ID_1,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/138275050-21ff9440-f9ab-4467-8ad7-c2847c064014.json")));
    MockPaymentAPI.doPayment(
        CALLER_ID_MLA_1,
        CLIENT_ID_MLA_1,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/4141386674.json")));

    final Response response =
        given()
            .body(
                IOUtils.toString(
                    getClass().getResourceAsStream("/paymentRequestBody/bodyComplete.json")))
            .with()
            .contentType("application/json")
            .post(uriBuilder.build());

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
  public void payments_blackLabelAccountMoney_200() throws URISyntaxException, IOException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/payments")
            .addParameter(CALLER_ID, String.valueOf(CALLER_ID_MLA_1))
            .addParameter(CLIENT_ID, String.valueOf(CLIENT_ID_MLA_1))
            .addParameter(PUBLIC_KEY, PUBLIC_KEY_BLACKLABEL_AM);
    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_BLACKLABEL_AM,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
    MockPreferenceAPI.getById(
        PREFERENCE_ID_BLACKLABEL_AM,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/384414502-d095679d-f7d9-4653-ad71-4fb5feda3494.json")));
    MockPaymentAPI.doPayment(
        CALLER_ID_MLA_1,
        CLIENT_ID_MLA_1,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/4141386674.json")));
    MockMerchantOrderAPI.createMerchantOrder(
        "395662610",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/merchantOrders/merchantOrderResponse.json")));
    MockTedAPI.getTed(
        204318018L,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/ted/validTedResponse.json")));

    final Response response =
        given()
            .body(
                IOUtils.toString(
                    getClass()
                        .getResourceAsStream("/paymentRequestBody/blackLabelAccountMoney.json")))
            .with()
            .contentType("application/json")
            .post(uriBuilder.build());

    assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    assertThat(response.getBody().print(), is(not(nullValue())));
  }

  @Test
  public void payments_validation_program_empty_400() throws URISyntaxException, IOException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/payments")
            .addParameter(CALLER_ID, String.valueOf(CALLER_ID_MLA_1))
            .addParameter(CLIENT_ID, String.valueOf(CLIENT_ID_MLA_1))
            .addParameter(PUBLIC_KEY, PUBLIC_KEY_BLACKLABEL_AM);
    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_BLACKLABEL_AM,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
    MockPreferenceAPI.getById(
        PREFERENCE_ID_BLACKLABEL_AM,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/384414502-d095679d-f7d9-4653-ad71-4fb5feda3494.json")));
    MockPaymentAPI.doPayment(
        CALLER_ID_MLA_1,
        CLIENT_ID_MLA_1,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/4141386674.json")));
    MockMerchantOrderAPI.createMerchantOrder(
        "395662610",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/merchantOrders/merchantOrderResponse.json")));
    MockTedAPI.getTed(
        204318018L,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/ted/validTedResponse.json")));

    final Response response =
        given()
            .body(
                IOUtils.toString(
                    getClass()
                        .getResourceAsStream("/paymentRequestBody/validationProgramEmpty.json")))
            .with()
            .contentType("application/json")
            .post(uriBuilder.build());

    assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
    assertThat(response.getBody().print(), is(not(nullValue())));
  }

  @Test
  public void payments_invalidUsers_400() throws URISyntaxException, IOException {
    final URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/payments")
            .addParameter(CALLER_ID, String.valueOf(CALLER_ID_MLA))
            .addParameter(CLIENT_ID, String.valueOf(CLIENT_ID_MLA))
            .addParameter(PUBLIC_KEY, PUBLIC_KEY_MLA_1);
    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_MLA_1,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
    MockPreferenceAPI.getById(
        PREFERENCE_ID_1,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/138275050-21ff9440-f9ab-4467-8ad7-c2847c064014.json")));
    MockPaymentAPI.doPayment(
        CALLER_ID_MLA,
        CLIENT_ID_MLA,
        HttpStatus.SC_BAD_REQUEST,
        IOUtils.toString(getClass().getResourceAsStream("/payment/400_invalidUser.json")));
    MockMerchantOrderAPI.createMerchantOrder(
        "395662610",
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/merchantOrders/merchantOrderResponse.json")));

    final Response response =
        given()
            .body(
                IOUtils.toString(
                    getClass()
                        .getResourceAsStream("/paymentRequestBody/bodyWithAccountMoney.json")))
            .with()
            .contentType("application/json")
            .post(uriBuilder.build());

    assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
    assertThat(response.getBody().print(), is(not(nullValue())));
  }

  @Test
  public void payments_unsupportPaymentMethods_400() throws URISyntaxException, IOException {
    final URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/payments")
            .addParameter(CALLER_ID, String.valueOf(CALLER_ID_MLA))
            .addParameter(CLIENT_ID, String.valueOf(CLIENT_ID_MLA))
            .addParameter(PUBLIC_KEY, PUBLIC_KEY_MLA_1);

    final Response response =
        given()
            .body(
                IOUtils.toString(
                    getClass()
                        .getResourceAsStream("/paymentRequestBody/bodyWithTwoPaymentMethods.json")))
            .with()
            .contentType("application/json")
            .post(uriBuilder.build());

    assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
    assertThat(response.getBody().print(), is(not(nullValue())));
  }

  @Test
  public void payments_withoutPK_400() throws URISyntaxException, IOException {
    final URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/payments")
            .addParameter(CALLER_ID, String.valueOf(CALLER_ID_MLA))
            .addParameter(CLIENT_ID, String.valueOf(CLIENT_ID_MLA));

    final Response response =
        given()
            .body(
                IOUtils.toString(
                    getClass()
                        .getResourceAsStream("/paymentRequestBody/bodyWithAccountMoney.json")))
            .with()
            .contentType("application/json")
            .post(uriBuilder.build());

    assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
    assertThat(response.getBody().print(), is(not(nullValue())));
  }

  @Test
  public void payments_blackLabelInvalidPref_400() throws URISyntaxException, IOException {
    final URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/payments")
            .addParameter(CALLER_ID, String.valueOf(CALLER_ID_MLA))
            .addParameter(CLIENT_ID, String.valueOf(CLIENT_ID_MLA))
            .addParameter(PUBLIC_KEY, PUBLIC_KEY_BLACKLABEL_AM);

    MockPublicKeyAPI.getPublicKey(
        PUBLIC_KEY_BLACKLABEL_AM,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream("/publicKey/TEST-d783da36-74a2-4378-85d1-76f498ca92c4.json")));
    MockPreferenceAPI.getById(
        PREFERENCE_ID_BLACKLABEL_AM,
        HttpStatus.SC_BAD_REQUEST,
        IOUtils.toString(getClass().getResourceAsStream("/preference/preferenceNotFound.json")));

    final Response response =
        given()
            .body(
                IOUtils.toString(
                    getClass()
                        .getResourceAsStream("/paymentRequestBody/blackLabelAccountMoney.json")))
            .with()
            .contentType("application/json")
            .post(uriBuilder.build());

    assertThat(response.getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
    assertThat(response.getBody().print(), is(not(nullValue())));
  }
}
