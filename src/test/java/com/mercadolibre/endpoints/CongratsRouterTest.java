package com.mercadolibre.endpoints;

import static com.mercadolibre.constants.QueryParamsConstants.IFPE;
import static com.mercadolibre.constants.QueryParamsConstants.PAYMENT_IDS;
import static com.mercadolibre.constants.QueryParamsConstants.PLATFORM;
import static com.mercadolibre.px.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.constants.CommonParametersNames.CALLER_SITE_ID;
import static com.mercadolibre.px.constants.CommonParametersNames.CLIENT_ID;
import static com.mercadolibre.px.constants.HeadersConstants.DENSITY;
import static com.mercadolibre.px.constants.HeadersConstants.PRODUCT_ID;
import static com.mercadolibre.utils.HeadersUtils.X_LOCATION_ENABLED;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockLoyaltyApi;
import com.mercadolibre.api.MockMerchAPI;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
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
  private static final String FLOW_NAME = "paymentsBlackLabel";
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
        new CongratsRequest(
            USER_ID_TEST,
            CLIENT_ID_TEST,
            Site.MLA.name(),
            PAYMENT_IDS_TEST,
            PLATFORM_TEST_MP,
            USER_AGENT_IOS,
            DENSITY,
            PRODUCT_ID_1,
            CAMPAIGN_ID_TEST,
            FLOW_NAME,
            false,
            null,
            null,
            "false",
            null,
            null);

    MockLoyaltyApi.getAsyncPoints(
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

    MockLoyaltyApi.getAsyncPoints(
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

    MockLoyaltyApi.getAsyncPoints(
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

    MockLoyaltyApi.getAsyncPoints(
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
  public void getCongrats_onlyDiscountsTimeoutPoints_200() throws URISyntaxException, IOException {
    URIBuilder uriBuilder =
        new URIBuilder("/px_mobile/congrats")
            .addParameter(CALLER_ID, USER_ID_TEST)
            .addParameter(CLIENT_ID, CLIENT_ID_TEST)
            .addParameter(CALLER_SITE_ID, Site.MLA.name())
            .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
            .addParameter(PLATFORM, PLATFORM_TEST_MP);

    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();

    MockLoyaltyApi.getAsyncPoints(congratsRequest, HttpStatus.SC_GATEWAY_TIMEOUT, "");

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
                    new Header(PRODUCT_ID, PRODUCT_ID_1),
                    new Header(X_LOCATION_ENABLED, "true")))
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
    when(congratsRequest.getFlowName()).thenReturn(FLOW_NAME);
    return congratsRequest;
  }
}
