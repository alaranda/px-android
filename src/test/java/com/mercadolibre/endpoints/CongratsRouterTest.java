package com.mercadolibre.endpoints;

import static com.mercadolibre.constants.QueryParamsConstants.FLOW_NAME;
import static com.mercadolibre.constants.QueryParamsConstants.IFPE;
import static com.mercadolibre.constants.QueryParamsConstants.PAYMENT_IDS;
import static com.mercadolibre.constants.QueryParamsConstants.PLATFORM;
import static com.mercadolibre.px.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.constants.CommonParametersNames.CALLER_SITE_ID;
import static com.mercadolibre.px.constants.CommonParametersNames.CLIENT_ID;
import static com.mercadolibre.px.constants.HeadersConstants.DENSITY;
import static com.mercadolibre.px.constants.HeadersConstants.PRODUCT_ID;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.PIX;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockCurrencyAPI;
import com.mercadolibre.api.MockLoyaltyApi;
import com.mercadolibre.api.MockMerchAPI;
import com.mercadolibre.api.MockPaymentAPI;
import com.mercadolibre.api.MockPaymentMethodSearchAPI;
import com.mercadolibre.api.MockSiteAPI;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.px.toolkit.utils.FileParserUtils;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import spark.utils.IOUtils;

public class CongratsRouterTest {

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  private static final String USER_ID_TEST = "123456789";
  private static final String CLIENT_ID_TEST = "0000000";
  private static final String PAYMENT_IDS_TEST = "1,2";
  private static final String PLATFORM_TEST_MP = "MP";
  private static final String DENSITY_XXHDPI = "xxhdpi";
  private static final UserAgent USER_AGENT_IOS = UserAgent.create("PX/iOS/4.3.4");
  private static final String PRODUCT_ID_1 = "BJEO9NVBF6RG01IIIOTG";
  private static final String CAMPAIGN_ID_TEST = "12345678";
  private static final String FLOW_NAME_TEST = "paymentsBlackLabel";
  private static final Headers HEADERS =
      new Headers(
          new Header("accept-language", "es-AR"),
          new Header("user-agent", "PX/iOS/4.3.4"),
          new Header(DENSITY, DENSITY_XXHDPI),
          new Header(PRODUCT_ID, PRODUCT_ID_1));

  @Test
  public void getCongrats_allNodesResponse_200() throws URISyntaxException, IOException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
            .addParameter(PLATFORM, PLATFORM_TEST_MP)
            .addParameter(IFPE, "true");

    final CongratsRequest congratsRequest =
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(Site.MLA.name())
            .paymentIds(PAYMENT_IDS_TEST)
            .platform(PLATFORM_TEST_MP)
            .userAgent(USER_AGENT_IOS)
            .density(DENSITY)
            .productId(PRODUCT_ID_1)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME_TEST)
            .build();

    MockLoyaltyApi.getAsyncPointsFromPayments(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));

    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

    final Response response = given().headers(HEADERS).get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  @Test
  public void getCongrats_onlyPoints_200() throws URISyntaxException, IOException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
            .addParameter(PLATFORM, PLATFORM_TEST_MP);

    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();

    MockLoyaltyApi.getAsyncPointsFromPayments(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));

    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_NOT_FOUND,
        IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponse404.json")));

    final Response response = given().headers(HEADERS).get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  @Test
  public void getCongrats_onlyCrossSelling_200() throws URISyntaxException, IOException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
            .addParameter(PLATFORM, PLATFORM_TEST_MP);

    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();

    MockLoyaltyApi.getAsyncPointsFromPayments(
        congratsRequest,
        HttpStatus.SC_NOT_FOUND,
        IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponse404.json")));

    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/merch/merchResponseOnlyCrossSelling.json")));

    final Response response = given().headers(HEADERS).get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  @Test
  public void getCongrats_onlyDiscounts_200() throws URISyntaxException, IOException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
            .addParameter(PLATFORM, PLATFORM_TEST_MP);

    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();

    MockLoyaltyApi.getAsyncPointsFromPayments(
        congratsRequest,
        HttpStatus.SC_NOT_FOUND,
        IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponse404.json")));

    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseOnlyDiscounts.json")));

    final Response response = given().headers(HEADERS).get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  @Test
  public void getCongrats_onlyInstructions_200() throws URISyntaxException, IOException {
    final String siteId = Site.MLA.name();
    final String paymentId = "1212323224";
    final URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, siteId)
            .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
            .addParameter(PLATFORM, PLATFORM_TEST_MP);

    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();

    MockLoyaltyApi.getAsyncPointsFromPayments(
        congratsRequest,
        HttpStatus.SC_NOT_FOUND,
        FileParserUtils.getStringResponseFromFile("/loyalty/loyalResponse404.json"));

    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/merch/merchResponseOnlyDiscounts.json"));

    MockPaymentAPI.getPayment(
        paymentId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/payment/pix_response_payment.json"));

    MockSiteAPI.getSiteAsync(
        siteId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/site/200_MLB_site.json"));

    MockCurrencyAPI.getCurrencyAsync(
        "BRL",
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/site/200_BRL_currency.json"));

    MockPaymentMethodSearchAPI.getPaymentMethodsAsync(
        siteId,
        "NONE",
        PIX,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile(
            "/paymentMethods/payment_methods_response_200.json"));

    final Response response = given().headers(HEADERS).get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  @Test
  public void getCongrats_onlyDiscountsTimeoutPoints_200() throws URISyntaxException, IOException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
            .addParameter(PLATFORM, PLATFORM_TEST_MP);

    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();

    MockLoyaltyApi.getAsyncPointsFromPayments(congratsRequest, HttpStatus.SC_GATEWAY_TIMEOUT, "");

    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseOnlyDiscounts.json")));

    final Response response = given().headers(HEADERS).get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  @Test
  public void getCongrats_paymentsIdEmpty_200() throws URISyntaxException, IOException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PAYMENT_IDS, "")
            .addParameter(PLATFORM, PLATFORM_TEST_MP);

    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();

    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseOnlyDiscounts.json")));

    final Response response = given().headers(HEADERS).get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  @Test
  public void getCongrats_paymentsIdNullString_200() throws URISyntaxException, IOException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PAYMENT_IDS, "null")
            .addParameter(PLATFORM, PLATFORM_TEST_MP);

    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();

    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseOnlyDiscounts.json")));

    final Response response = given().headers(HEADERS).get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  @Test
  public void getCongrats_withoutPaymentsId_200() throws URISyntaxException, IOException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PAYMENT_IDS, "")
            .addParameter(PLATFORM, PLATFORM_TEST_MP);

    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();

    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseOnlyDiscounts.json")));

    final Response response =
        given()
            .headers(
                new Headers(
                    new Header("accept-language", "es-AR"),
                    new Header("user-agent", "PX/iOS/4.3.4"),
                    new Header(DENSITY, DENSITY_XXHDPI),
                    new Header(PRODUCT_ID, PRODUCT_ID_1)))
            .get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  @Test
  public void getCongrats_invalidParamsCallerId_callerIdIsRequired() throws URISyntaxException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
            .addParameter(PLATFORM, PLATFORM_TEST_MP);

    final Response response = given().headers(HEADERS).get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
  }

  @Test
  public void getCongrats_invalidParamsPlatform_platformIsRequired() throws URISyntaxException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST);

    final Response response = given().headers(HEADERS).get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
  }

  @Test
  public void getCongrats_invalidHeaderProductId_productIdIsRequired() throws URISyntaxException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PLATFORM, PLATFORM_TEST_MP)
            .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST);

    final Response response =
        given()
            .headers(
                new Headers(
                    new Header("accept-language", "es_AR"),
                    new Header("user-agent", "PX/iOS/4.3.4"),
                    new Header(DENSITY, DENSITY)))
            .get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
  }

  @Test
  public void getCongrats_invalidHeaderDensity_densityIsRequired() throws URISyntaxException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PLATFORM, PLATFORM_TEST_MP);

    final Response response =
        given()
            .headers(
                new Headers(
                    new Header("accept-language", "es_AR"),
                    new Header("user-agent", "PX/iOS/4.3.4"),
                    new Header(PRODUCT_ID, PRODUCT_ID_1)))
            .get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
  }

  @Test
  public void getCongratsFlowProximity_200() throws URISyntaxException, IOException {
    final String proximityFlowName = "proximity";
    final String paymentId = "444";
    final String purchaseId = "555";
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
            .addParameter(PLATFORM, PLATFORM_TEST_MP)
            .addParameter(FLOW_NAME, proximityFlowName);

    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();
    when(congratsRequest.getFlowName()).thenReturn(proximityFlowName);
    when(congratsRequest.getPaymentIds()).thenReturn(paymentId);

    MockPaymentAPI.getPayment(
        paymentId,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/payment/4141386674_with_external_reference.json")));

    MockLoyaltyApi.getAsyncPointsFromPurchase(
        getDefaultCongratsRequestMock(),
        purchaseId,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));

    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_NOT_FOUND,
        IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponse404.json")));

    final Response response = given().headers(HEADERS).get(uriBuilder.build());
    assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  private CongratsRequest getDefaultCongratsRequestMock() {

    final CongratsRequest congratsRequest = Mockito.mock(CongratsRequest.class);
    when(congratsRequest.getUserId()).thenReturn(USER_ID_TEST);
    when(congratsRequest.getClientId()).thenReturn(CLIENT_ID_TEST);
    when(congratsRequest.getSiteId()).thenReturn(Site.MLA.name());
    when(congratsRequest.getPaymentIds()).thenReturn(PAYMENT_IDS_TEST);
    when(congratsRequest.getPlatform()).thenReturn(PLATFORM_TEST_MP);
    when(congratsRequest.getUserAgent()).thenReturn(USER_AGENT_IOS);
    when(congratsRequest.getDensity()).thenReturn(DENSITY);
    when(congratsRequest.getProductId()).thenReturn(PRODUCT_ID);
    when(congratsRequest.getFlowName()).thenReturn(FLOW_NAME_TEST);
    return congratsRequest;
  }
}
