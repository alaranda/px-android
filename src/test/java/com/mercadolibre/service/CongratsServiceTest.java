package com.mercadolibre.service;

import static com.mercadolibre.px.dto.lib.site.Site.MLB;
import static com.mercadolibre.px.dto.lib.site.Site.MLM;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.ACCOUNT_MONEY;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.MERCADOPAGO_CC;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.OXXO;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.PIX;
import static com.mercadolibre.utils.Translations.CONGRATS_THIRD_PARTY_CARD_INFO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import com.mercadolibre.api.MockCurrencyAPI;
import com.mercadolibre.api.MockKycVaultV2Dao;
import com.mercadolibre.api.MockLoyaltyApi;
import com.mercadolibre.api.MockMerchAPI;
import com.mercadolibre.api.MockPaymentAPI;
import com.mercadolibre.api.MockPaymentMethodSearchAPI;
import com.mercadolibre.api.MockPreferenceAPI;
import com.mercadolibre.api.MockSiteAPI;
import com.mercadolibre.dto.congrats.Congrats;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.dto.congrats.OperationInfoHierarchy;
import com.mercadolibre.dto.congrats.OperationInfoType;
import com.mercadolibre.helper.MockTestHelper;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.payment.PxPaymentType;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.px.toolkit.utils.FileParserUtils;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import com.mercadolibre.utils.Translations;
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
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(Site.MLA.name())
            .paymentIds(PAYMENT_IDS_TEST)
            .platform(Platform.MP.getId())
            .userAgent(invalidUserAgent)
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .build();

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
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(Site.MLU.name())
            .paymentIds(PAYMENT_IDS_TEST)
            .platform(Platform.MP.getId())
            .userAgent(invalidUserAgent)
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .build();

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
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(Site.MLA.name())
            .paymentIds(PAYMENT_IDS_TEST)
            .platform(Platform.ML.getId())
            .userAgent(invalidUserAgent)
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .build();

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
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(siteId)
            .paymentIds(paymentId)
            .platform(Platform.MP.getId())
            .userAgent(UserAgent.create("PX/Android/4.40.0"))
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .preferenceId(prefId)
            .build();

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
      getPointsDiscountsAndInstructions_OfflineMethod_WithCredentials_returnRedirectUrlAndInstructions()
          throws ApiException {

    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String paymentId = "1212323224";
    final String siteId = MLB.getSiteId();
    final CongratsRequest congratsRequest =
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(siteId)
            .paymentIds(paymentId)
            .platform(Platform.MP.getId())
            .userAgent(UserAgent.create("PX/Android/4.40.0"))
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .preferenceId(prefId)
            .accessToken("TEST-3792603160086480-033021-54e7ad29181cdcd4da6e7eb49d73f53d-139274850")
            .publicKey("TEST-d1a694aa-b0ee-4dd2-8326-79b1d53a676c")
            .paymentTypeId(PxPaymentType.BANK_TRANSFER.getType())
            .build();

    MockPreferenceAPI.getById(
        prefId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/preference/preferenceWithRedirectUrl.json"));

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
  public void
      getPointsDiscountsAndInstructions_OfflineMethod_WithCredentialsAndPaymentTypeId_returnRedirectUrlAndInstructions()
          throws ApiException {

    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String paymentId = "1212323224";
    final String siteId = MLB.getSiteId();
    final CongratsRequest congratsRequest =
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(siteId)
            .paymentIds(paymentId)
            .platform(Platform.MP.getId())
            .userAgent(UserAgent.create("PX/Android/4.40.0"))
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .preferenceId(prefId)
            .accessToken("TEST-3792603160086480-033021-54e7ad29181cdcd4da6e7eb49d73f53d-139274850")
            .publicKey("TEST-d1a694aa-b0ee-4dd2-8326-79b1d53a676c")
            .paymentTypeId(PxPaymentType.BANK_TRANSFER.getType())
            .build();

    MockPreferenceAPI.getById(
        prefId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/preference/preferenceWithRedirectUrl.json"));

    MockPaymentAPI.getPayment(
        paymentId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/payment/7eleven_response_payments.json"));

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
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(siteId)
            .paymentIds("null")
            .platform(Platform.MP.getId())
            .userAgent(UserAgent.create("PX/Android/4.40.0"))
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .preferenceId(prefId)
            .build();

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
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(siteId)
            .paymentIds(paymentId + ",23432432")
            .platform(Platform.MP.getId())
            .userAgent(UserAgent.create("PX/Android/4.40.0"))
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .preferenceId(prefId)
            .build();

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
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(siteId)
            .platform(Platform.MP.getId())
            .userAgent(UserAgent.create("PX/Android/4.40.0"))
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .preferenceId(prefId)
            .build();

    MockPreferenceAPI.getById(
        prefId,
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/preference/preferenceWithMalformedBackUrl.json")));

    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);

    assertEquals(congrats.getBackUrl(), "http://back-url-success .com");
  }

  @Test
  public void testGetPointsAndDiscounts_notThirdPartyCard_dontReturnOperationInfoNode()
      throws IOException, ApiException {
    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String paymentId = "4141386674";
    final String siteId = Site.MLA.getSiteId();
    final CongratsRequest congratsRequest =
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(siteId)
            .paymentIds(paymentId + ",23432432")
            .platform(Platform.MP.getId())
            .userAgent(UserAgent.create("PX/Android/4.40.0"))
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .preferenceId(prefId)
            .build();

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
        IOUtils.toString(
            getClass().getResourceAsStream("/payment/4141386674_with_card_holder.json")));

    MockKycVaultV2Dao.getKycVaultUserData(
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/kyc/user_11111111_dni_45464778.json")));

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

    assertNull(congrats.getOperationInfo());
  }

  @Test
  public void testGetPointsAndDiscounts_kyc200OKWithErrorInBody_dontReturnOperationInfoNode()
      throws IOException, ApiException {
    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String paymentId = "4141386674";
    final String siteId = Site.MLA.getSiteId();
    final CongratsRequest congratsRequest =
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(siteId)
            .paymentIds(paymentId + ",23432432")
            .platform(Platform.MP.getId())
            .userAgent(UserAgent.create("PX/Android/4.40.0"))
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .preferenceId(prefId)
            .build();

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
        IOUtils.toString(
            getClass().getResourceAsStream("/payment/4141386674_with_card_holder.json")));

    MockKycVaultV2Dao.getKycVaultUserData(
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/kyc/200_OK_with_error_in_body.json")));

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

    assertNull(congrats.getOperationInfo());
  }

  @Test
  public void testGetPointsAndDiscounts_isThirdPartyCard_returnOperationInfoNode()
      throws IOException, ApiException {
    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String paymentId = "4141386674";
    final String siteId = Site.MLA.getSiteId();
    final CongratsRequest congratsRequest =
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(siteId)
            .paymentIds(paymentId + ",23432432")
            .platform(Platform.MP.getId())
            .userAgent(UserAgent.create("PX/Android/4.40.0"))
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .preferenceId(prefId)
            .build();

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
        IOUtils.toString(
            getClass().getResourceAsStream("/payment/4141386674_with_card_holder.json")));

    MockKycVaultV2Dao.getKycVaultUserData(
        HttpStatus.SC_OK,
        IOUtils.toString(
            getClass().getResourceAsStream("/kyc/user_22314151_cuil_20147360194.json")));

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

    assertNotNull(congrats.getOperationInfo());
    assertEquals(OperationInfoType.NEUTRAL.getValue(), congrats.getOperationInfo().getType());
    assertEquals(
        OperationInfoHierarchy.QUIET.getValue(), congrats.getOperationInfo().getHierarchy());
    assertEquals(
        Translations.INSTANCE.getTranslationByLocale(
            CONTEXT_ES.getLocale(), CONGRATS_THIRD_PARTY_CARD_INFO),
        congrats.getOperationInfo().getBody());
  }

  @Test
  public void getPointsDiscountsAndInstructions_offlineMethod_siteError_emptyCongrats()
      throws ApiException {

    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String paymentId = "1212323224";
    final String siteId = MLB.getSiteId();
    final CongratsRequest congratsRequest =
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(siteId)
            .paymentIds(paymentId)
            .platform(Platform.MP.getId())
            .userAgent(UserAgent.create("PX/Android/4.40.0"))
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .preferenceId(prefId)
            .accessToken("TEST-3792603160086480-033021-54e7ad29181cdcd4da6e7eb49d73f53d-139274850")
            .publicKey("TEST-d1a694aa-b0ee-4dd2-8326-79b1d53a676c")
            .paymentTypeId(PxPaymentType.BANK_TRANSFER.getType())
            .build();

    MockPreferenceAPI.getById(
        prefId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/preference/preferenceWithRedirectUrl.json"));

    MockPaymentAPI.getPayment(
        paymentId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/payment/pix_response_payment.json"));

    MockSiteAPI.getSiteAsync(
        siteId,
        HttpStatus.SC_INTERNAL_SERVER_ERROR,
        FileParserUtils.getStringResponseFromFile("/site/404_site_response.json"));

    when(CONTEXT_ES.getSite()).thenReturn(MLB);
    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);
    assertNotNull(congrats.getInstructions());
    assertEquals(
        congrats.getInstructions().getTitle(), "Seu código Pix de R$ 1.189,00 já pode ser pago");
  }

  @Test
  public void getPointsDiscountsAndInstructions_offlineMethod_pmSearchError_emptyCongrats()
      throws ApiException {

    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String paymentId = "1212323224";
    final String siteId = MLM.getSiteId();
    final CongratsRequest congratsRequest =
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(siteId)
            .paymentIds(paymentId)
            .platform(Platform.MP.getId())
            .userAgent(UserAgent.create("PX/Android/4.40.0"))
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .preferenceId(prefId)
            .accessToken("TEST-3792603160086480-033021-54e7ad29181cdcd4da6e7eb49d73f53d-139274850")
            .publicKey("TEST-d1a694aa-b0ee-4dd2-8326-79b1d53a676c")
            .paymentTypeId(PxPaymentType.BANK_TRANSFER.getType())
            .build();

    MockPreferenceAPI.getById(
        prefId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/preference/preferenceWithRedirectUrl.json"));

    MockPaymentAPI.getPayment(
        paymentId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/payment/oxxo_response_payment.json"));

    MockSiteAPI.getSiteAsync(
        siteId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/site/200_MLM_site.json"));

    MockCurrencyAPI.getCurrencyAsync(
        "MXN",
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/site/200_MXN_currency.json"));

    MockPaymentMethodSearchAPI.getPaymentMethodsAsync(
        siteId,
        "NONE",
        PIX,
        HttpStatus.SC_NOT_FOUND,
        FileParserUtils.getStringResponseFromFile(
            "/paymentMethods/payment_methods_response_404.json"));

    when(CONTEXT_ES.getSite()).thenReturn(MLM);
    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(CONTEXT_ES, congratsRequest);
    assertNull(congrats.getInstructions());
  }

  @Test
  public void getPointsDiscountsAndInstructions_offlineMethod_completeInstruction()
      throws ApiException {

    RequestMockHolder.clear();
    final String prefId = "138275050-69faf356-c9b3-47d2-afe1-43d924fb6876";
    final String paymentId = "1212323224";
    final String siteId = MLM.getSiteId();
    final CongratsRequest congratsRequest =
        CongratsRequest.builder()
            .userId(USER_ID_TEST)
            .clientId(CLIENT_ID_TEST)
            .siteId(siteId)
            .paymentIds(paymentId)
            .platform(Platform.MP.getId())
            .userAgent(UserAgent.create("PX/Android/4.40.0"))
            .density(DENSITY)
            .productId(PRODUCT_ID_INSTORE)
            .campaignId(CAMPAIGN_ID_TEST)
            .flowName(FLOW_NAME)
            .preferenceId(prefId)
            .accessToken("TEST-3792603160086480-033021-54e7ad29181cdcd4da6e7eb49d73f53d-139274850")
            .publicKey("TEST-d1a694aa-b0ee-4dd2-8326-79b1d53a676c")
            .paymentTypeId(PxPaymentType.TICKET.getType())
            .build();

    MockPreferenceAPI.getById(
        prefId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/preference/preferenceWithRedirectUrl.json"));

    MockPaymentAPI.getPayment(
        paymentId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/payment/oxxo_response_payment.json"));

    MockSiteAPI.getSiteAsync(
        siteId,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/site/200_MLM_site.json"));

    MockCurrencyAPI.getCurrencyAsync(
        "MXN",
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/site/200_MXN_currency.json"));

    MockPaymentMethodSearchAPI.getPaymentMethodsAsync(
        siteId,
        "NONE",
        OXXO,
        HttpStatus.SC_OK,
        FileParserUtils.getStringResponseFromFile("/paymentMethods/payment_methods_oxxo_200.json"));

    final Context context = MockTestHelper.mockContextLibDto();
    when(context.getSite()).thenReturn(MLM);
    final Congrats congrats =
        congratsService.getPointsDiscountsAndInstructions(context, congratsRequest);
    assertNotNull(congrats.getInstructions());
  }
}
