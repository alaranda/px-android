package com.mercadolibre.service;

import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.ACCOUNT_MONEY;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.MERCADOPAGO_CC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockLoyaltyApi;
import com.mercadolibre.api.MockMerchAPI;
import com.mercadolibre.dto.congrats.Congrats;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import spark.utils.IOUtils;

public class CongratsServiceTest {

  @Before
  public void before() {
    RequestMockHolder.clear();
  }

  private static final String USER_ID_TEST = "11111111";
  private static final String CLIENT_ID_TEST = "0000000";
  private static final String PAYMENT_IDS_TEST = "333,222";
  private static final String PLATFORM_OTHER = "OTHER";
  private static final String DENSITY = "xxhdpi";
  private static final String PRODUCT_ID_INSTORE = "bh3215f10flg01nmhg6g";
  private static final String PRODUCT_ID = "test";
  private static final String CAMPAIGN_ID_TEST = "5656565656";
  private static final String FLOW_NAME = "paymentsBlackLabel";
  private static final UserAgent USER_AGENT_IOS = UserAgent.create("PX/iOS/4.5.0");

  private static final CongratsService congratsService = new CongratsService();

  public static final String REQUEST_ID = UUID.randomUUID().toString();
  public static final Context CONTEXT_ES =
      Context.builder().requestId(REQUEST_ID).locale("es-AR").build();

  @Test
  public void getPointsAndDiscounts_validParams_crossSellingDiscountsAndPoints()
      throws IOException {

    MockLoyaltyApi.getAsyncPoints(
        getDefaultCongratsRequestMock(),
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));

    MockMerchAPI.getAsyncCrosselingAndDiscount(
        getDefaultCongratsRequestMock(),
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

    final Congrats congrats =
        congratsService.getPointsAndDiscounts(CONTEXT_ES, getDefaultCongratsRequestMock());

    assertThat(congrats.getCrossSelling(), notNullValue());
    assertThat(congrats.hasDiscounts(), is(true));
    assertThat(congrats.hasPoints(), is(true));
    assertThat(congrats.getCustomOrder(), is(false));
  }

  @Test
  public void getPointsAndDiscounts_invalidUserAgentIOS_crossSellingAndDiscounts()
      throws IOException {

    final UserAgent invalidUserAgent = UserAgent.create("PX/iOS/4.22");
    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();
    when(congratsRequest.getUserAgent()).thenReturn(invalidUserAgent);

    MockLoyaltyApi.getAsyncPoints(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));
    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

    final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

    assertThat(congrats.getCrossSelling(), notNullValue());
    assertThat(congrats.hasDiscounts(), is(true));
    assertThat(congrats.hasPoints(), is(false));
    assertThat(congrats.getCustomOrder(), is(false));
  }

  @Test
  public void getPointsAndDiscounts_platformOTHER_LoyaltyTargetMP() throws IOException {

    final UserAgent invalidUserAgent = UserAgent.create("PX/iOS/4.25");
    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();
    when(congratsRequest.getUserAgent()).thenReturn(invalidUserAgent);
    when(congratsRequest.getPlatform()).thenReturn(PLATFORM_OTHER);

    MockLoyaltyApi.getAsyncPoints(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));
    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

    final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

    assertThat(congrats.getMpuntos().getAction().getTarget(), is("meli://hub"));
    assertThat(congrats.getCustomOrder(), is(false));
  }

  @Test
  public void getPointsAndDiscounts_invalidUserAgentAndroid_crossSellingAndDiscounts()
      throws IOException {

    final UserAgent invalidUserAgent = UserAgent.create("PX/Android/4.23.0");
    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();
    when(congratsRequest.getUserAgent()).thenReturn(invalidUserAgent);

    MockLoyaltyApi.getAsyncPoints(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));
    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

    final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

    assertThat(congrats.getCrossSelling(), notNullValue());
    assertThat(congrats.hasDiscounts(), is(true));
    assertThat(congrats.hasPoints(), is(false));
    assertThat(congrats.getCustomOrder(), is(false));
  }

  @Test
  public void
      getPointsAndDiscounts_validUserAgentAndroidOnMercadopago_crossSellingAndDiscountsCustomOrderAndExpenseSplit()
          throws IOException {

    final UserAgent invalidUserAgent = UserAgent.create("PX/Android/4.23.2");
    final CongratsRequest congratsRequest =
        new CongratsRequest(
            USER_ID_TEST,
            CLIENT_ID_TEST,
            Site.MLA.name(),
            PAYMENT_IDS_TEST,
            Platform.MP.getId(),
            invalidUserAgent,
            DENSITY,
            PRODUCT_ID_INSTORE,
            CAMPAIGN_ID_TEST,
            FLOW_NAME,
            false,
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

    final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

    assertThat(congrats.getCrossSelling(), notNullValue());
    assertThat(congrats.hasDiscounts(), is(true));
    assertThat(congrats.hasPoints(), is(true));
    assertThat(congrats.getCustomOrder(), is(true));

    assertThat(
        congrats.getExpenseSplit().getTitle().getMessage(),
        is("Podés dividir este gasto con tus contactos"));
    assertThat(congrats.getExpenseSplit().getTitle().getBackgroundColor(), is("#ffffff"));
    assertThat(congrats.getExpenseSplit().getTitle().getTextColor(), is("#333333"));
    assertThat(congrats.getExpenseSplit().getTitle().getWeight(), is("semi_bold"));

    assertThat(congrats.getExpenseSplit().getAction().getLabel(), is("Dividir gasto"));
    assertThat(
        congrats.getExpenseSplit().getAction().getTarget(),
        is(
            "mercadopago://mplayer/money_split_external?operation_id=333&source=paymentsBlackLabel"));

    assertThat(
        congrats.getExpenseSplit().getImageUrl(),
        is(
            "https://mobile.mercadolibre.com/remote_resources/image/px_congrats_money_split_mp?density=xxhdpi&locale=es_AR"));
  }

  @Test
  public void
      getPointsAndDiscounts_validUserAgentAndroidOnMercadopagoAndMLU_crossSellingAndDiscountsCustomOrderAndNoExpenseSplit()
          throws IOException {

    final UserAgent invalidUserAgent = UserAgent.create("PX/Android/4.23.2");
    final CongratsRequest congratsRequest =
        new CongratsRequest(
            USER_ID_TEST,
            CLIENT_ID_TEST,
            Site.MLU.name(),
            PAYMENT_IDS_TEST,
            Platform.MP.getId(),
            invalidUserAgent,
            DENSITY,
            PRODUCT_ID_INSTORE,
            CAMPAIGN_ID_TEST,
            FLOW_NAME,
            false,
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

    final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

    assertThat(congrats.getCrossSelling(), notNullValue());
    assertThat(congrats.hasDiscounts(), is(true));
    assertThat(congrats.hasPoints(), is(true));
    assertThat(congrats.getCustomOrder(), is(true));

    assertThat(congrats.getExpenseSplit(), is(nullValue()));
  }

  @Test
  public void
      getPointsAndDiscounts_validUserAgentAndroidOnMercadoLibre_crossSellingAndDiscountsCustomOrderAndExpenseSplit()
          throws IOException {

    final UserAgent invalidUserAgent = UserAgent.create("PX/Android/4.23.2");
    final CongratsRequest congratsRequest =
        new CongratsRequest(
            USER_ID_TEST,
            CLIENT_ID_TEST,
            Site.MLA.name(),
            PAYMENT_IDS_TEST,
            Platform.ML.getId(),
            invalidUserAgent,
            DENSITY,
            PRODUCT_ID_INSTORE,
            CAMPAIGN_ID_TEST,
            FLOW_NAME,
            false,
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

    final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

    assertThat(congrats.getCrossSelling(), notNullValue());
    assertThat(congrats.hasDiscounts(), is(true));
    assertThat(congrats.hasPoints(), is(true));
    assertThat(congrats.getCustomOrder(), is(true));

    assertThat(
        congrats.getExpenseSplit().getTitle().getMessage(),
        is("Podés dividir este gasto con tus contactos"));
    assertThat(congrats.getExpenseSplit().getTitle().getBackgroundColor(), is("#ffffff"));
    assertThat(congrats.getExpenseSplit().getTitle().getTextColor(), is("#333333"));
    assertThat(congrats.getExpenseSplit().getTitle().getWeight(), is("semi_bold"));

    assertThat(congrats.getExpenseSplit().getAction().getLabel(), is("Dividir gasto"));
    assertThat(
        congrats.getExpenseSplit().getAction().getTarget(),
        is(
            "mercadolibre://mplayer/money_split_external?operation_id=333&source=paymentsBlackLabel"));

    assertThat(
        congrats.getExpenseSplit().getImageUrl(),
        is(
            "https://mobile.mercadolibre.com/remote_resources/image/px_congrats_money_split_mp?density=xxhdpi&locale=es_AR"));
  }

  @Test
  public void getPointsAndDiscounts_MPIOSPxVersion_linkLoyaltyBlank() throws IOException {

    final UserAgent invalidUserAgent = UserAgent.create("PX/iOS/4.24.2");
    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();
    when(congratsRequest.getUserAgent()).thenReturn(invalidUserAgent);

    MockLoyaltyApi.getAsyncPoints(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/loyalty/loyalResponseOk.json")));
    MockMerchAPI.getAsyncCrosselingAndDiscount(
        congratsRequest,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/merch/merchResponseCrossSellingAndDiscounts.json")));

    final Congrats congrats = congratsService.getPointsAndDiscounts(CONTEXT_ES, congratsRequest);

    assertThat(congrats.getMpuntos().getAction().getTarget(), is(""));
    assertThat(congrats.getCustomOrder(), is(false));
    assertThat(congrats.getPaymentMethodsImages(), nullValue());
  }

  @Test
  public void getPointsAndDiscounts_mlmIfpe_viewReceiptAndIfpeCompliance() {

    final Context context = Mockito.mock(Context.class);
    when(context.getLocale()).thenReturn(new Locale("es", "MX"));
    when(context.getRequestId()).thenReturn(REQUEST_ID);
    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();
    when(congratsRequest.isIfpe()).thenReturn(true);
    when(congratsRequest.getSiteId()).thenReturn("MLM");
    when(congratsRequest.getPaymentMethodsIds()).thenReturn(ACCOUNT_MONEY);

    final Congrats congrats = congratsService.getPointsAndDiscounts(context, congratsRequest);

    assertThat(congrats.getViewReceipt().getTarget(), notNullValue());
    assertThat(congrats.getTopTextBox().getMessage(), notNullValue());
    assertThat(congrats.getCustomOrder(), is(false));
    assertThat(congrats.getPaymentMethodsImages().size(), is(1));
  }

  @Test
  public void getPointsAndDiscounts_withOutAccountMoney_viewReceiptNull() {

    final Context context = Mockito.mock(Context.class);
    when(context.getLocale()).thenReturn(new Locale("es", "MX"));
    when(context.getRequestId()).thenReturn(REQUEST_ID);
    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();
    when(congratsRequest.isIfpe()).thenReturn(true);
    when(congratsRequest.getSiteId()).thenReturn("MLM");
    when(congratsRequest.getPaymentMethodsIds()).thenReturn(MERCADOPAGO_CC);

    final Congrats congrats = congratsService.getPointsAndDiscounts(context, congratsRequest);

    assertThat(congrats.getViewReceipt(), nullValue());
    assertThat(congrats.getTopTextBox(), nullValue());
    assertThat(congrats.getCustomOrder(), is(false));
    assertThat(congrats.getPaymentMethodsImages().size(), is(1));
  }

  private CongratsRequest getDefaultCongratsRequestMock() {

    final CongratsRequest congratsRequest = Mockito.mock(CongratsRequest.class);
    when(congratsRequest.getUserId()).thenReturn(USER_ID_TEST);
    when(congratsRequest.getClientId()).thenReturn(CLIENT_ID_TEST);
    when(congratsRequest.getSiteId()).thenReturn(Site.MLA.name());
    when(congratsRequest.getPaymentIds()).thenReturn(PAYMENT_IDS_TEST);
    when(congratsRequest.getPlatform()).thenReturn(Platform.MP.getId());
    when(congratsRequest.getUserAgent()).thenReturn(USER_AGENT_IOS);
    when(congratsRequest.getDensity()).thenReturn(DENSITY);
    when(congratsRequest.getProductId()).thenReturn(PRODUCT_ID);
    when(congratsRequest.getFlowName()).thenReturn(FLOW_NAME);
    when(congratsRequest.getPaymentMethodsIds()).thenReturn(null);
    return congratsRequest;
  }
}
