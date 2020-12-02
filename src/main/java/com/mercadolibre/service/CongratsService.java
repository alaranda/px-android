package com.mercadolibre.service;

import static com.mercadolibre.constants.Constants.BUTTON_CONTINUE;
import static com.mercadolibre.constants.Constants.BUTTON_LOUD;
import static com.mercadolibre.constants.Constants.IFPE_MESSAGE_COLOR;
import static com.mercadolibre.constants.Constants.PX_PM_ODR;
import static com.mercadolibre.constants.DatadogMetricsNames.CONGRATS_ERROR_BUILD_CONGRATS;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.ACCOUNT_MONEY;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;

import com.mercadolibre.api.LoyaltyApi;
import com.mercadolibre.api.MerchAPI;
import com.mercadolibre.api.PreferenceAPI;
import com.mercadolibre.dto.congrats.Action;
import com.mercadolibre.dto.congrats.AutoReturn;
import com.mercadolibre.dto.congrats.Congrats;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.dto.congrats.CrossSelling;
import com.mercadolibre.dto.congrats.Discounts;
import com.mercadolibre.dto.congrats.ExpenseSplit;
import com.mercadolibre.dto.congrats.Points;
import com.mercadolibre.dto.congrats.merch.MerchResponse;
import com.mercadolibre.px.dto.lib.button.Button;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.platform.Platform;
import com.mercadolibre.px.dto.lib.preference.BackUrls;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.dto.lib.preference.RedirectUrls;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.dto.lib.text.Text;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.dto.Version;
import com.mercadolibre.px.toolkit.dto.user_agent.OperatingSystem;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.services.OnDemandResourcesService;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogUtils;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.UrlDownloadUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.utils.StringUtils;

public class CongratsService {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final List<String> INSTORE_PRODUCT_IDS =
      Arrays.asList(
          "bh31umv10flg01nmhg60",
          "bh31u3v10flg01nmhg5g",
          "bh321atmiu3001hkebmg",
          "bh3215f10flg01nmhg6g",
          "bknneuko5mpg01jd8mfg",
          "bknnfpko5mpg01jd8mg0",
          "bknng4ko5mpg01jd8mgg",
          "bknnga4o5mpg01jd8mh0",
          "bckm077hau10018ovch0");

  private static final List<String> EXPENSE_SPLIT_PRODUCT_IDS =
      Arrays.asList(
          "bh31umv10flg01nmhg60",
          "bh31u3v10flg01nmhg5g",
          "bh321atmiu3001hkebmg",
          "bh3215f10flg01nmhg6g",
          "bknneuko5mpg01jd8mfg",
          "bknnfpko5mpg01jd8mg0",
          "bknng4ko5mpg01jd8mgg",
          "bknnga4o5mpg01jd8mh0",
          "bckm077hau10018ovch0",
          "bckjo7qbvkh001fp9vag",
          "bckjnp7hau10018ovcd0",
          "bckjocnhau10018ovcg0",
          "bckjo2vhau10018ovce0");

  private static final String ACTIVITIES_LINK = "mercadopago://activities_v2_list";

  private static final Pattern SPLIT_BY_COMMA_PATTERN = Pattern.compile(",");

  private static final String ODR_IMAGES_VERSION = "1";

  private static final String EXPENSE_SPLIT_ML_DEEPLINK =
      "mercadolibre://mplayer/money_split_external?operation_id=%s&source=%s";
  private static final String EXPENSE_SPLIT_MP_DEEPLINK =
      "mercadopago://mplayer/money_split_external?operation_id=%s&source=%s";
  private static final String EXPENSE_SPLIT_TEXT_COLOR = "#333333";
  private static final String EXPENSE_SPLIT_WEIGHT = "semi_bold";
  private static final String EXPENSE_SPLIT_ODR_ICON_KEY = "px_congrats_money_split_mp";

  public static final Version WITHOUT_LOYALTY_CONGRATS_IOS = Version.create("4.22");
  public static final Version WITHOUT_LOYALTY_CONGRATS_ANDROID = Version.create("4.23.1");

  private static final String EXPENSE_SPLIT_BACKGROUND_COLOUR = "#ffffff";

  private static final int RETURNING_TIME_SECONDS = 5;

  public CongratsService() {}

  /**
   * Retorna los puntos sumados en el pago y los acmulados mas los descuentos otorgados.
   *
   * @param context context
   * @param congratsRequest congrats request
   * @return Congrats congrats object
   */
  public Congrats getPointsAndDiscounts(
      final Context context, final CongratsRequest congratsRequest) throws ApiException {

    CompletableFuture<Either<Points, ApiError>> futureLoyalPoints = null;
    // TODO La comparacion con "null" esta por un bug donde me pasan el parametro en null y se
    // transforma a string. Sacar validacion cuando muera esa version.
    if (StringUtils.isNotBlank(congratsRequest.getPaymentIds())
        && !congratsRequest.getPaymentIds().equalsIgnoreCase("null")
        && userAgentIsValid(congratsRequest.getUserAgent())) {
      futureLoyalPoints = LoyaltyApi.INSTANCE.getAsyncPoints(context, congratsRequest);
    }

    final CompletableFuture<Either<MerchResponse, ApiError>> futureMerchResponse =
        MerchAPI.INSTANCE.getAsyncCrossSellingAndDiscount(context, congratsRequest);

    CompletableFuture<Either<Preference, ApiError>> futurePref = null;
    if (null != congratsRequest.getPreferenceId()) {
      futurePref =
          PreferenceAPI.INSTANCE.geAsynctPreference(context, congratsRequest.getPreferenceId());
    }

    Points points = null;
    Set<CrossSelling> crossSelling = null;
    Discounts discounts = null;

    final Optional<Points> optionalPoints =
        LoyaltyApi.INSTANCE.getPointsFromFuture(context, futureLoyalPoints);
    try {
      if (optionalPoints.isPresent()) {
        final Points loyalPoints = optionalPoints.get();
        if (null != loyalPoints.getProgress()
            && null != loyalPoints.getAction()
            && null != loyalPoints.getTitle()) {
          points =
              new Points.Builder(loyalPoints.getProgress(), loyalPoints.getTitle())
                  .action(
                      loyalPoints.getAction(),
                      congratsRequest.getPlatform(),
                      congratsRequest.getUserAgent())
                  .build();
        }
      }

      Optional<MerchResponse> optionalMerchResponse =
          MerchAPI.INSTANCE.getMerchResponseFromFuture(context, futureMerchResponse);

      if (optionalMerchResponse.isPresent()) {
        final MerchResponse merchResponse = optionalMerchResponse.get();
        if (null != merchResponse.getCrossSelling()) {
          crossSelling = new HashSet<>();

          String iconUrl = null;

          if (merchResponse.getCrossSelling().getContent() != null) {
            iconUrl =
                OnDemandResourcesService.createOnDemandResourcesUrl(
                    merchResponse.getCrossSelling().getContent().getIcon(),
                    congratsRequest.getDensity(),
                    context.getLocale().toString());
          }

          crossSelling.add(
              new CrossSelling.Builder(merchResponse.getCrossSelling().getContent(), iconUrl)
                  .build());
        }

        if (null != merchResponse.getDiscounts()
            && !merchResponse.getDiscounts().getItems().isEmpty()) {
          final String downloadUrl =
              UrlDownloadUtils.buildDownloadUrl(congratsRequest.getPlatform());
          discounts =
              new Discounts.Builder(
                      context,
                      merchResponse.getDiscounts(),
                      congratsRequest.getPlatform(),
                      downloadUrl)
                  .build();
        }
      }

      Optional<Preference> optionalPreferenceResponse =
          PreferenceAPI.INSTANCE.getPreferenceFromFuture(context, futurePref);

      Button primaryButton = null;
      String backUrl = null;
      String redirectUrl = null;
      AutoReturn autoReturn = null;

      if (optionalPreferenceResponse.isPresent()) {
        Preference preference = optionalPreferenceResponse.get();
        backUrl = getBackUrl(preference.getBackUrls());
        redirectUrl = getRedirectUrl(preference.getRedirectUrls());
        if (hasValidAutoreturn(preference.getAutoReturn()) && StringUtils.isNotBlank(backUrl)) {
          primaryButton = buildPrimaryButton(context.getLocale());
          autoReturn =
              new AutoReturn(
                  Translations.INSTANCE.getTranslationByLocale(
                      context.getLocale(), Translations.RETURNING_MERCHANT_SITE),
                  RETURNING_TIME_SECONDS);
        }
      }

      final Action viewReceipt =
          viewReceipt(
              context.getLocale(),
              congratsRequest.getSiteId(),
              congratsRequest.getPaymentMethodsIds());

      final Text ifpeCompliance =
          textIfpeCompliance(
              congratsRequest.isIfpe(),
              congratsRequest.getPaymentMethodsIds(),
              context.getLocale());

      return new Congrats(
          points,
          discounts,
          crossSelling,
          viewReceipt,
          ifpeCompliance,
          isCustomOrderEnabled(congratsRequest.getProductId()),
          generateExpenseSplitNode(context.getLocale(), congratsRequest),
          buildPaymentMethodsImages(context, congratsRequest),
          primaryButton,
          null,
          backUrl,
          redirectUrl,
          autoReturn);
    } catch (Exception e) {
      METRIC_COLLECTOR.incrementCounter(CONGRATS_ERROR_BUILD_CONGRATS);
      LOGGER.error(
          LogUtils.getServiceExceptionLog(
              context, "Congrats Service", congratsRequest.toString(), e));
      return new Congrats();
    }
  }

  private ExpenseSplit generateExpenseSplitNode(
      final Locale locale, final CongratsRequest congratsRequest) {

    if (!EXPENSE_SPLIT_PRODUCT_IDS.stream()
            .anyMatch(p -> p.equalsIgnoreCase(congratsRequest.getProductId()))
        || StringUtils.isEmpty(congratsRequest.getPaymentIds())) {
      return null;
    }

    if (Site.MLU.getSiteId().equalsIgnoreCase(congratsRequest.getSiteId())) {
      return null;
    }

    Text title =
        new Text(
            Translations.INSTANCE.getTranslationByLocale(locale, Translations.EXPENSE_SPLIT_TITLE),
            EXPENSE_SPLIT_BACKGROUND_COLOUR,
            EXPENSE_SPLIT_TEXT_COLOR,
            EXPENSE_SPLIT_WEIGHT);

    String deeplink = EXPENSE_SPLIT_MP_DEEPLINK;

    // TODO: Remove platform from the congratsRequest and use the one in Context
    if (Platform.ML.getId().equalsIgnoreCase(congratsRequest.getPlatform())) {
      deeplink = EXPENSE_SPLIT_ML_DEEPLINK;
    }

    Iterator<String> paymentIdsIt =
        Arrays.asList(SPLIT_BY_COMMA_PATTERN.split(congratsRequest.getPaymentIds())).iterator();

    if (!paymentIdsIt.hasNext()) {
      return null;
    }

    String paymentId = paymentIdsIt.next();

    deeplink = String.format(deeplink, paymentId, congratsRequest.getFlowName());

    Action action =
        new Action(
            Translations.INSTANCE.getTranslationByLocale(
                locale, Translations.EXPENSE_SPLIT_BUTTON_TITLE),
            deeplink);

    String icon =
        OnDemandResourcesService.createOnDemandResourcesUrl(
            EXPENSE_SPLIT_ODR_ICON_KEY, congratsRequest.getDensity(), locale.toString());

    return new ExpenseSplit(title, action, icon);
  }

  private boolean isCustomOrderEnabled(final String productId) {
    return INSTORE_PRODUCT_IDS.stream().anyMatch(p -> p.equalsIgnoreCase(productId));
  }

  /**
   * Valida que para la version XXX de IOS no se devuelva puntos.
   *
   * @param userAgent user agent
   * @return boolean user agent is valid
   */
  private boolean userAgentIsValid(final UserAgent userAgent) {

    // Validacion iOS
    if (userAgent.getOperatingSystem().getName().equals(OperatingSystem.IOS.getName())
        && userAgent
            .getVersion()
            .getVersionName()
            .equals(WITHOUT_LOYALTY_CONGRATS_IOS.getVersionName())) {
      return false;
    }

    // Validacion Android
    if (userAgent.getOperatingSystem().getName().equals(OperatingSystem.ANDROID.getName())
        && WITHOUT_LOYALTY_CONGRATS_ANDROID.compareTo(userAgent.getVersion()) == 1) {
      return false;
    }

    return true;
  }

  private Text textIfpeCompliance(
      final boolean ifpe, final String paymentMethodsIds, final Locale locale) {

    if (ifpe && validateAccountMoneyId(paymentMethodsIds)) {
      return new Text(
          Translations.INSTANCE.getTranslationByLocale(
              locale, Translations.IFPE_COMPLIANCE_MESSAGE),
          null,
          IFPE_MESSAGE_COLOR,
          null);
    }

    return null;
  }

  private Action viewReceipt(
      final Locale locale, final String siteId, final String paymentMethodsIds) {

    if (Site.MLM.getSiteId().equalsIgnoreCase(siteId)
        && validateAccountMoneyId(paymentMethodsIds)) {
      return new Action(
          Translations.INSTANCE.getTranslationByLocale(locale, Translations.VIEW_RECEIPT),
          ACTIVITIES_LINK);
    }

    return null;
  }

  private boolean validateAccountMoneyId(final String paymentMethodsIds) {

    if (paymentMethodsIds != null && paymentMethodsIds.contains(ACCOUNT_MONEY)) {
      return true;
    }
    return false;
  }

  private Map<String, String> buildPaymentMethodsImages(
      final Context context, final CongratsRequest congratsRequest) {

    if (StringUtils.isBlank(congratsRequest.getPaymentMethodsIds())) {
      return null;
    }

    final List<String> paymentMethodsIdList =
        Arrays.asList(SPLIT_BY_COMMA_PATTERN.split(congratsRequest.getPaymentMethodsIds()));

    Map<String, String> paymentMethodImages = new HashMap<>();
    paymentMethodsIdList.forEach(
        paymentMethodId -> {
          paymentMethodImages.put(
              paymentMethodId,
              OnDemandResourcesService.createOnDemandResourcesUrl(
                  String.format(PX_PM_ODR, paymentMethodId),
                  congratsRequest.getDensity(),
                  context.getLocale().toString(),
                  ODR_IMAGES_VERSION));
        });

    return paymentMethodImages;
  }

  private Button buildPrimaryButton(final Locale locale) {

    return Button.builder()
        .action(BUTTON_CONTINUE)
        .label(
            Translations.INSTANCE.getTranslationByLocale(locale, Translations.RETURN_MERCHANT_SITE))
        .type(BUTTON_LOUD)
        .build();
  }

  private String getBackUrl(final BackUrls backUrls) {

    if (null != backUrls && StringUtils.isNotBlank(backUrls.getSuccess())) {
      return backUrls.getSuccess();
    }

    return null;
  }

  private String getRedirectUrl(final RedirectUrls redirectUrl) {

    if (null != redirectUrl && StringUtils.isNotBlank(redirectUrl.getSuccess())) {
      return redirectUrl.getSuccess();
    }

    return null;
  }

  private boolean hasValidAutoreturn(final String autoreturn) {

    if (autoreturn.equalsIgnoreCase("all") || autoreturn.equalsIgnoreCase("approved")){
      return true;
    }
    return false;
  }

}
