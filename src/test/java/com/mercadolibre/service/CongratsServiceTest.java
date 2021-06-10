package com.mercadolibre.service;

import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.ACCOUNT_MONEY;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.MERCADOPAGO_CC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockInstructionsAPI;
import com.mercadolibre.api.MockLoyaltyApi;
import com.mercadolibre.api.MockMerchAPI;
import com.mercadolibre.api.MockPaymentAPI;
import com.mercadolibre.api.MockPreferenceAPI;
import com.mercadolibre.dto.congrats.Congrats;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.helper.MockTestHelper;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
import java.util.Locale;
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

  public static final Context CONTEXT_ES = MockTestHelper.mockContextLibDto();

  @Test
  public void getPointsAndDiscounts_validParams_crossSellingDiscountsAndPoints()
      throws IOException, ApiException {

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
        congratsService.getPointsDiscountsAndInstructions(
            CONTEXT_ES, getDefaultCongratsRequestMock());

    assertThat(congrats.getCrossSelling(), notNullValue());
    assertThat(congrats.hasDiscounts(), is(true));
    assertThat(congrats.hasPoints(), is(true));
    assertThat(congrats.getCustomOrder(), is(false));
  }

  @Test
  public void getPointsAndDiscounts_invalidUserAgentIOS_crossSellingAndDiscounts()
      throws IOException, ApiException {

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

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);

    assertThat(congrats.getCrossSelling(), notNullValue());
    assertThat(congrats.hasDiscounts(), is(true));
    assertThat(congrats.hasPoints(), is(false));
    assertThat(congrats.getCustomOrder(), is(false));
  }

  @Test
  public void getPointsAndDiscounts_platformOTHER_LoyaltyTargetMP()
      throws IOException, ApiException {

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

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);

    assertThat(congrats.getMpuntos().getAction().getTarget(), is("meli://hub"));
    assertThat(congrats.getCustomOrder(), is(false));
  }

  @Test
  public void getPointsAndDiscounts_invalidUserAgentAndroid_crossSellingAndDiscounts()
      throws IOException, ApiException {

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

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);

    assertThat(congrats.getCrossSelling(), notNullValue());
    assertThat(congrats.hasDiscounts(), is(true));
    assertThat(congrats.hasPoints(), is(false));
    assertThat(congrats.getCustomOrder(), is(false));
  }

  @Test
  public void
      getPointsAndDiscounts_validUserAgentAndroidOnMercadopago_crossSellingAndDiscountsCustomOrderAndExpenseSplit()
          throws IOException, ApiException {

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
            null,
            null,
            null,
            null,
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

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);

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
          throws IOException, ApiException {

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
            null,
            null,
            null,
            null,
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

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);

    assertThat(congrats.getCrossSelling(), notNullValue());
    assertThat(congrats.hasDiscounts(), is(true));
    assertThat(congrats.hasPoints(), is(true));
    assertThat(congrats.getCustomOrder(), is(true));

    assertThat(congrats.getExpenseSplit(), is(nullValue()));
  }

  @Test
  public void
      getPointsAndDiscounts_validUserAgentAndroidOnMercadoLibre_crossSellingAndDiscountsCustomOrderAndExpenseSplit()
          throws IOException, ApiException {

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
            null,
            null,
            null,
            null,
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

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);

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
  public void getPointsAndDiscounts_MPIOSPxVersion_linkLoyaltyBlank()
      throws IOException, ApiException {

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

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);

    assertThat(congrats.getMpuntos().getAction().getTarget(), is(""));
    assertThat(congrats.getCustomOrder(), is(false));
    assertThat(congrats.getPaymentMethodsImages(), nullValue());
  }

  @Test
  public void getPointsAndDiscounts_mlmIfpe_viewReceiptAndIfpeCompliance() throws ApiException {

    final Context context = MockTestHelper.mockContextLibDto();
    when(context.getLocale()).thenReturn(new Locale("es", "MX"));

    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();
    when(congratsRequest.isIfpe()).thenReturn(true);
    when(congratsRequest.getSiteId()).thenReturn("MLM");
    when(congratsRequest.getPaymentMethodsIds()).thenReturn(ACCOUNT_MONEY);

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(context, congratsRequest);

    assertThat(congrats.getViewReceipt().getTarget(), notNullValue());
    assertThat(congrats.getTopTextBox().getMessage(), notNullValue());
    assertThat(congrats.getCustomOrder(), is(false));
    assertThat(congrats.getPaymentMethodsImages().size(), is(1));
  }

  @Test
  public void getPointsAndDiscounts_withOutAccountMoney_viewReceiptNull() throws ApiException {

    final Context context = MockTestHelper.mockContextLibDto();
    when(context.getLocale()).thenReturn(new Locale("es", "MX"));
    final CongratsRequest congratsRequest = getDefaultCongratsRequestMock();
    when(congratsRequest.isIfpe()).thenReturn(true);
    when(congratsRequest.getSiteId()).thenReturn("MLM");
    when(congratsRequest.getPaymentMethodsIds()).thenReturn(MERCADOPAGO_CC);

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(context, congratsRequest);

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

  @Test
  public void getPointsAndDiscounts_preferenceId_returnRedirectUrl()
      throws IOException, ApiException {

    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String paymentId = "4141386674";
    final String siteId = Site.MLA.getSiteId();
    final CongratsRequest congratsRequest =
        new CongratsRequest(
            USER_ID_TEST,
            CLIENT_ID_TEST,
            siteId,
            paymentId,
            Platform.MP.getId(),
            UserAgent.create("PX/Android/4.40.0"),
            DENSITY,
            PRODUCT_ID_INSTORE,
            CAMPAIGN_ID_TEST,
            FLOW_NAME,
            false,
            null,
            prefId,
            null,
            null,
            null,
            null);

    MockPreferenceAPI.getById(
        prefId,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/preference/preferenceWithRedirectUrl.json")));

    MockPaymentAPI.getPayment(
        paymentId,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/4141386674.json")));

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);

    assertEquals(
        "http://redirect-url-success.com"
            + "?status=approved"
            + "&collection_status=approved"
            + "&external_reference="
            + "&preference_id="
            + prefId
            + "&site_id="
            + siteId
            + "&merchant_order_id"
            + "&merchant_account_id"
            + "&collection_id="
            + paymentId
            + "&payment_id="
            + paymentId
            + "&payment_type=credit_card"
            + "&processing_mode=aggregator",
        congrats.getRedirectUrl());
  }

  @Test
  public void
      getPointsDiscountsAndInstructions_OfflineMethod_WithoutCredentials_returnRedirectUrlAndNullInstructions()
          throws IOException, ApiException {

    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String paymentId = "1212323224";
    final String siteId = Site.MLB.getSiteId();
    final CongratsRequest congratsRequest =
        new CongratsRequest(
            USER_ID_TEST,
            CLIENT_ID_TEST,
            siteId,
            paymentId,
            Platform.MP.getId(),
            UserAgent.create("PX/Android/4.40.0"),
            DENSITY,
            PRODUCT_ID_INSTORE,
            CAMPAIGN_ID_TEST,
            FLOW_NAME,
            false,
            null,
            prefId,
            null,
            null,
            null,
            null);

    MockPreferenceAPI.getById(
        prefId,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/preference/preferenceWithRedirectUrl.json")));

    MockPaymentAPI.getPayment(
        paymentId,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/pix_response_payment.json")));

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);
    assertNull(congrats.getInstructions());
    assertEquals(
        "http://redirect-url-success.com"
            + "?status=approved"
            + "&collection_status=approved"
            + "&external_reference="
            + "&preference_id="
            + prefId
            + "&site_id="
            + siteId
            + "&merchant_order_id"
            + "&merchant_account_id"
            + "&collection_id="
            + paymentId
            + "&payment_id="
            + paymentId
            + "&payment_type"
            + "&processing_mode",
        congrats.getRedirectUrl());
  }

  @Test
  public void
      getPointsDiscountsAndInstructions_OfflineMethod_WithCredentials_returnRedirectUrlAndInstructions()
          throws IOException, ApiException {

    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String paymentId = "1212323224";
    final String siteId = Site.MLB.getSiteId();
    final CongratsRequest congratsRequest =
        new CongratsRequest(
            USER_ID_TEST,
            CLIENT_ID_TEST,
            siteId,
            paymentId,
            Platform.MP.getId(),
            UserAgent.create("PX/Android/4.40.0"),
            DENSITY,
            PRODUCT_ID_INSTORE,
            CAMPAIGN_ID_TEST,
            FLOW_NAME,
            false,
            null,
            prefId,
            null,
            null,
            "TEST-3792603160086480-033021-54e7ad29181cdcd4da6e7eb49d73f53d-139274850",
            "TEST-d1a694aa-b0ee-4dd2-8326-79b1d53a676c");

    MockPreferenceAPI.getById(
        prefId,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/preference/preferenceWithRedirectUrl.json")));

    MockPaymentAPI.getPayment(
        paymentId,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/pix_response_payment.json")));

    MockInstructionsAPI.getInstructions(
        paymentId,
        "TEST-3792603160086480-033021-54e7ad29181cdcd4da6e7eb49d73f53d-139274850",
        "TEST-d1a694aa-b0ee-4dd2-8326-79b1d53a676o",
        "ticket",
        IOUtils.toString(getClass().getResourceAsStream("/instructions/MLB_instructions.json")),
        HttpStatus.SC_OK);

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);
    assertNotNull(congrats.getInstructions());
    assertEquals(
        "http://redirect-url-success.com"
            + "?status=approved"
            + "&collection_status=approved"
            + "&external_reference="
            + "&preference_id="
            + prefId
            + "&site_id="
            + siteId
            + "&merchant_order_id"
            + "&merchant_account_id"
            + "&collection_id="
            + paymentId
            + "&payment_id="
            + paymentId
            + "&payment_type"
            + "&processing_mode",
        congrats.getRedirectUrl());
  }

  @Test
  public void getPointsAndDiscounts_preferenceId_returnAutoReturnAndPrimaryButton()
      throws IOException, ApiException {

    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String siteId = Site.MLA.getSiteId();
    final CongratsRequest congratsRequest =
        new CongratsRequest(
            USER_ID_TEST,
            CLIENT_ID_TEST,
            siteId,
            "null",
            Platform.MP.getId(),
            UserAgent.create("PX/Android/4.40.0"),
            DENSITY,
            PRODUCT_ID_INSTORE,
            CAMPAIGN_ID_TEST,
            FLOW_NAME,
            false,
            null,
            prefId,
            null,
            null,
            null,
            null);

    MockPreferenceAPI.getById(
        prefId,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/preference/preferenceWithBackUrlAndAutoReturn.json")));

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);

    assertEquals(congrats.getAutoReturn().getLabel(), "Te llevaremos de vuelta al sitio en {0}");
    assertEquals(congrats.getAutoReturn().getSeconds(), 5);
    assertEquals(
        congrats.getBackUrl(),
        "http://back-url-success.com"
            + "?status=approved"
            + "&collection_status=approved"
            + "&external_reference="
            + "&preference_id="
            + prefId
            + "&site_id="
            + siteId
            + "&merchant_order_id"
            + "&merchant_account_id");
  }

  @Test
  public void getPointsAndDiscounts_preferenceId_returnBackUrl() throws IOException, ApiException {
    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String paymentId = "4141386674";
    final String siteId = Site.MLA.getSiteId();
    final CongratsRequest congratsRequest =
        new CongratsRequest(
            USER_ID_TEST,
            CLIENT_ID_TEST,
            siteId,
            paymentId + ",23432432",
            Platform.MP.getId(),
            UserAgent.create("PX/Android/4.40.0"),
            DENSITY,
            PRODUCT_ID_INSTORE,
            CAMPAIGN_ID_TEST,
            FLOW_NAME,
            false,
            null,
            prefId,
            null,
            null,
            null,
            null);

    MockPreferenceAPI.getById(
        prefId,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass()
                .getResourceAsStream(
                    "/preference/138275050-69faf356-c9b3-47d2-afe1-43d924fb6876.json")));

    MockPaymentAPI.getPayment(
        paymentId,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/payment/4141386674.json")));

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);

    assertEquals(
        "http://back-url-success.com"
            + "?status=approved"
            + "&collection_status=approved"
            + "&external_reference="
            + "&preference_id="
            + prefId
            + "&site_id="
            + siteId
            + "&merchant_order_id"
            + "&merchant_account_id"
            + "&collection_id="
            + paymentId
            + "&payment_id="
            + paymentId
            + "&payment_type=credit_card"
            + "&processing_mode=aggregator",
        congrats.getBackUrl());
  }

  @Test
  public void
      getPointsAndDiscounts_preferenceIdWithMalformedBackUrl_returnBackUrlWithoutAppendingAnyData()
          throws IOException, ApiException {

    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String siteId = Site.MLA.getSiteId();
    final CongratsRequest congratsRequest =
        new CongratsRequest(
            USER_ID_TEST,
            CLIENT_ID_TEST,
            siteId,
            null,
            Platform.MP.getId(),
            UserAgent.create("PX/Android/4.40.0"),
            DENSITY,
            PRODUCT_ID_INSTORE,
            CAMPAIGN_ID_TEST,
            FLOW_NAME,
            false,
            null,
            prefId,
            null,
            null,
            null,
            null);

    MockPreferenceAPI.getById(
        prefId,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/preference/preferenceWithMalformedBackUrl.json")));

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);

    assertEquals(congrats.getBackUrl(), "http://back-url-success .com");
  }
}
