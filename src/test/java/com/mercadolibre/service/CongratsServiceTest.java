package com.mercadolibre.service;

import com.mercadolibre.api.MockLoyaltyApi;
import com.mercadolibre.api.MockMerchAPI;
import com.mercadolibre.dto.congrats.Congrats;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CongratsServiceTest {

    @Before
    public void before() {
        RequestMockHolder.clear();
    }

    private static final String USER_ID_TEST = "11111111";
    private static final String CLIENT_ID_TEST = "0000000";
    private static final String PAYMENT_IDS_TEST = "333,222";
    private static final String PLATFORM_TEST_MP = "MP";
    private static final String PLATFORM_OTHER = "OTHER";
    private static final String DENSITY = "xxhdpi";
    private static final String PRODUCT_ID = "test";
    private static final String CAMPAIGN_ID_TEST = "5656565656";
    private static final String FLOW_NAME = "paymentsBlackLabel";
    private static final UserAgent USER_AGENT_IOS = UserAgent.create("PX/iOS/4.5.0");
    private static final Headers HEADERS = new Headers(new Header("accept-language", "es_AR"),
            new Header("user-agent", "PX/iOS/4.5.0"), new Header(DENSITY, DENSITY),
            new Header(PRODUCT_ID, PRODUCT_ID));
    private static final CongratsService congratsService = new CongratsService();

    public static final String REQUEST_ID = UUID.randomUUID().toString();
    public static final Context CONTEXT_ES = Context.builder().requestId(REQUEST_ID).locale("es_AR").build();


    @Test
    public void getPointsAndDiscounts_validParams_crossSellingDiscountsAndPoints() throws IOException {

        final CongratsRequest congratsRequest = new CongratsRequest(USER_ID_TEST, CLIENT_ID_TEST, Site.MLA.name(), PAYMENT_IDS_TEST, PLATFORM_TEST_MP, USER_AGENT_IOS, DENSITY, PRODUCT_ID, CAMPAIGN_ID_TEST, FLOW_NAME);

        MockLoyaltyApi.getAsyncPoints(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));

        MockMerchAPI.getAsyncCrosselingAndDiscount(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

        final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

        assertThat(congrats.getCrossSelling(), notNullValue());
        assertThat(congrats.hasDiscounts(), is(true));
        assertThat(congrats.hasPoints(), is(true));
    }

    @Test
    public void getPointsAndDiscounts_invalidUserAgentIOS_crossSellingAndDiscounts() throws IOException {

        final UserAgent invalidUserAgent = UserAgent.create("PX/iOS/4.22");
        final CongratsRequest congratsRequest = new CongratsRequest(USER_ID_TEST, CLIENT_ID_TEST, Site.MLA.name(), PAYMENT_IDS_TEST, PLATFORM_TEST_MP, invalidUserAgent, DENSITY, PRODUCT_ID, CAMPAIGN_ID_TEST, FLOW_NAME);

        MockLoyaltyApi.getAsyncPoints(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));
        MockMerchAPI.getAsyncCrosselingAndDiscount(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

        final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

        assertThat(congrats.getCrossSelling(), notNullValue());
        assertThat(congrats.hasDiscounts(), is(true));
        assertThat(congrats.hasPoints(), is(false));
    }

    @Test
    public void getPointsAndDiscounts_platformOTHER_LoyaltyTargetMP() throws IOException {

        final UserAgent invalidUserAgent = UserAgent.create("PX/iOS/4.25");
        final CongratsRequest congratsRequest = new CongratsRequest(USER_ID_TEST, CLIENT_ID_TEST, Site.MLA.name(), PAYMENT_IDS_TEST, PLATFORM_OTHER, invalidUserAgent, DENSITY, PRODUCT_ID, CAMPAIGN_ID_TEST, FLOW_NAME);

        MockLoyaltyApi.getAsyncPoints(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));
        MockMerchAPI.getAsyncCrosselingAndDiscount(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

        final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

        assertThat(congrats.getMpuntos().getAction().getTarget(), is("meli://hub"));
    }

    @Test
    public void getPointsAndDiscounts_invalidUserAgentAndroid_crossSellingAndDiscounts() throws IOException {

        final UserAgent invalidUserAgent = UserAgent.create("PX/Android/4.23.0");
        final CongratsRequest congratsRequest = new CongratsRequest(USER_ID_TEST, CLIENT_ID_TEST, Site.MLA.name(), PAYMENT_IDS_TEST, PLATFORM_TEST_MP, invalidUserAgent, DENSITY, PRODUCT_ID, CAMPAIGN_ID_TEST, FLOW_NAME);

        MockLoyaltyApi.getAsyncPoints(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));
        MockMerchAPI.getAsyncCrosselingAndDiscount(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

        final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

        assertThat(congrats.getCrossSelling(), notNullValue());
        assertThat(congrats.hasDiscounts(), is(true));
        assertThat(congrats.hasPoints(), is(false));
    }

    @Test
    public void getPointsAndDiscounts_validUserAgentAndroid_crossSellingAndDiscounts() throws IOException {

        final UserAgent invalidUserAgent = UserAgent.create("PX/Android/4.23.2");
        final CongratsRequest congratsRequest = new CongratsRequest(USER_ID_TEST, CLIENT_ID_TEST, Site.MLA.name(), PAYMENT_IDS_TEST, PLATFORM_TEST_MP, invalidUserAgent, DENSITY, PRODUCT_ID, CAMPAIGN_ID_TEST, FLOW_NAME);

        MockLoyaltyApi.getAsyncPoints(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));
        MockMerchAPI.getAsyncCrosselingAndDiscount(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

        final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

        assertThat(congrats.getCrossSelling(), notNullValue());
        assertThat(congrats.hasDiscounts(), is(true));
        assertThat(congrats.hasPoints(), is(true));
    }

    @Test
    public void getPointsAndDiscounts_MPIOSPxVersion_linkLoyaltyBlank() throws IOException {

        final UserAgent invalidUserAgent = UserAgent.create("PX/iOS/4.24.2");
        final CongratsRequest congratsRequest = new CongratsRequest(USER_ID_TEST, CLIENT_ID_TEST, Site.MLA.name(), PAYMENT_IDS_TEST, PLATFORM_TEST_MP, invalidUserAgent, DENSITY, PRODUCT_ID, CAMPAIGN_ID_TEST, FLOW_NAME);

        MockLoyaltyApi.getAsyncPoints(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));
        MockMerchAPI.getAsyncCrosselingAndDiscount(congratsRequest, HttpStatus.SC_OK, IOUtils.toString(getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

        final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

        assertThat(congrats.getMpuntos().getAction().getTarget(), is(""));
    }
}