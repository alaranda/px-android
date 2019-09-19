package com.mercadolibre.endpoints;

import com.mercadolibre.api.MockLoyaltyApi;
import com.mercadolibre.api.MockMerchAPI;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.dto.user_agent.OperatingSystem;
import com.mercadolibre.dto.user_agent.UserAgent;
import com.mercadolibre.dto.user_agent.Version;
import com.mercadolibre.px.toolkit.constants.Site;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.mercadolibre.constants.QueryParamsConstants.*;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_SITE_ID;
import static io.restassured.RestAssured.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CongratsRouterTest {

    @Before
    public void before() {
        RequestMockHolder.clear();
    }

    private static final String USER_ID_TEST = "123456789";
    private static final String CLIENT_ID_TEST = "0000000";
    private static final String PAYMENT_IDS_TEST = "1,2";
    private static final String PLATFORM_TEST_MP = "MP";

    private static final UserAgent userAgent = UserAgent.create("PX/iOS/4.3.4");


    @Test
    public void getCongrats_allNodesResponse_200() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/congrats")
                .addParameter(Constants.CALLER_ID_PARAM, USER_ID_TEST)
                .addParameter(Constants.CLIENT_ID_PARAM, CLIENT_ID_TEST)
                .addParameter(CALLER_SITE_ID, Site.MLA.getName())
                .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
                .addParameter(PLATFORM, PLATFORM_TEST_MP);

        final CongratsRequest congratsRequest = new CongratsRequest(USER_ID_TEST, CLIENT_ID_TEST, Site.MLA.getName(), PAYMENT_IDS_TEST, PLATFORM_TEST_MP, userAgent);

        MockLoyaltyApi.getAsyncPoints(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));

        MockMerchAPI.getAsyncCrosselingAndDiscount(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

        final Response response = get(uriBuilder.build());
        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @Test
    public void getCongrats_onlyPoints_200() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/congrats")
                .addParameter(Constants.CALLER_ID_PARAM, USER_ID_TEST)
                .addParameter(Constants.CLIENT_ID_PARAM, CLIENT_ID_TEST)
                .addParameter(CALLER_SITE_ID, Site.MLA.getName())
                .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
                .addParameter(PLATFORM, PLATFORM_TEST_MP);

        final CongratsRequest congratsRequest = new CongratsRequest(USER_ID_TEST, CLIENT_ID_TEST, Site.MLA.getName(), PAYMENT_IDS_TEST, PLATFORM_TEST_MP, userAgent);

        MockLoyaltyApi.getAsyncPoints(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));

        MockMerchAPI.getAsyncCrosselingAndDiscount(congratsRequest, HttpStatus.SC_NOT_FOUND, IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponse404.json")));

        final Response response = get(uriBuilder.build());
        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @Test
    public void getCongrats_onlyCrossSelling_200() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/congrats")
                .addParameter(Constants.CALLER_ID_PARAM, USER_ID_TEST)
                .addParameter(Constants.CLIENT_ID_PARAM, CLIENT_ID_TEST)
                .addParameter(CALLER_SITE_ID, Site.MLA.getName())
                .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
                .addParameter(PLATFORM, PLATFORM_TEST_MP);

        final CongratsRequest congratsRequest = new CongratsRequest(USER_ID_TEST, CLIENT_ID_TEST, Site.MLA.getName(), PAYMENT_IDS_TEST, PLATFORM_TEST_MP, userAgent);

        MockLoyaltyApi.getAsyncPoints(congratsRequest, HttpStatus.SC_NOT_FOUND, IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponse404.json")));

        MockMerchAPI.getAsyncCrosselingAndDiscount(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseOnlyCrossSelling.json")));

        final Response response = get(uriBuilder.build());
        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @Test
    public void getCongrats_onlyDiscounts_200() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/congrats")
                .addParameter(Constants.CALLER_ID_PARAM, USER_ID_TEST)
                .addParameter(Constants.CLIENT_ID_PARAM, CLIENT_ID_TEST)
                .addParameter(CALLER_SITE_ID, Site.MLA.getName())
                .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
                .addParameter(PLATFORM, PLATFORM_TEST_MP);

        final CongratsRequest congratsRequest = new CongratsRequest(USER_ID_TEST, CLIENT_ID_TEST, Site.MLA.getName(), PAYMENT_IDS_TEST, PLATFORM_TEST_MP, userAgent);

        MockLoyaltyApi.getAsyncPoints(congratsRequest, HttpStatus.SC_NOT_FOUND, IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponse404.json")));

        MockMerchAPI.getAsyncCrosselingAndDiscount(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseOnlyDiscounts.json")));

        final Response response = get(uriBuilder.build());
        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }

    @Test
    public void getCongrats_onlyDiscountsTimeoutPoints_200() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("/px_mobile/congrats")
                .addParameter(Constants.CALLER_ID_PARAM, USER_ID_TEST)
                .addParameter(Constants.CLIENT_ID_PARAM, CLIENT_ID_TEST)
                .addParameter(CALLER_SITE_ID, Site.MLA.getName())
                .addParameter(PAYMENT_IDS, PAYMENT_IDS_TEST)
                .addParameter(PLATFORM, PLATFORM_TEST_MP);

        final CongratsRequest congratsRequest = new CongratsRequest(USER_ID_TEST, CLIENT_ID_TEST, Site.MLA.getName(), PAYMENT_IDS_TEST, PLATFORM_TEST_MP, userAgent);

        MockLoyaltyApi.getAsyncPoints(congratsRequest, HttpStatus.SC_GATEWAY_TIMEOUT, "");

        MockMerchAPI.getAsyncCrosselingAndDiscount(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseOnlyDiscounts.json")));

        final Response response = get(uriBuilder.build());
        assertThat(response.getStatusCode(), is(HttpStatus.SC_OK));
    }
}
