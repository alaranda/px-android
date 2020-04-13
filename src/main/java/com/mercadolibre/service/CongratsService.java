package com.mercadolibre.service;

import static com.mercadolibre.constants.Constants.IFPE_MESSAGE_COLOR;
import static com.mercadolibre.constants.DatadogMetricsNames.CONGRATS_ERROR_BUILD_CONGRATS;
import static com.mercadolibre.px.toolkit.constants.PaymentMethodId.ACCOUNT_MONEY;
import static com.mercadolibre.px.toolkit.utils.monitoring.datadog.DatadogUtils.METRIC_COLLECTOR;

import com.mercadolibre.api.LoyaltyApi;
import com.mercadolibre.api.MerchAPI;
import com.mercadolibre.dto.congrats.Action;
import com.mercadolibre.dto.congrats.Congrats;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.dto.congrats.CrossSelling;
import com.mercadolibre.dto.congrats.Discounts;
import com.mercadolibre.dto.congrats.Points;
import com.mercadolibre.dto.congrats.merch.MerchResponse;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.text.Text;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.dto.Version;
import com.mercadolibre.px.toolkit.dto.user_agent.OperatingSystem;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogUtils;
import com.mercadolibre.utils.IfpeUtils;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.UrlDownloadUtils;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.utils.StringUtils;

public class CongratsService {

  private IfpeUtils ifpeUtils;

  public CongratsService(final IfpeUtils ifpeUtils) {
    this.ifpeUtils = ifpeUtils;
  }

  private static final Logger LOGGER = LogManager.getLogger();

  public static final Version WITHOUT_LOYALTY_CONGRATS_IOS = Version.create("4.22");
  public static final Version WITHOUT_LOYALTY_CONGRATS_ANDROID = Version.create("4.23.1");
  private static final String ACTIVITIES_LINK = "mercadopago://activities_v2_list";

  /**
   * Retorna los puntos sumados en el pago y los acmulados mas los descuentos otorgados.
   *
   * @param context context
   * @param congratsRequest congrats request
   * @return Congrats congrats object
   */
  public Congrats getPointsAndDiscounts(
      final Context context, final CongratsRequest congratsRequest) {

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
          final String iconUrl =
              OnDemandResources.createOnDemandResoucesUrlByContent(
                  congratsRequest,
                  merchResponse.getCrossSelling().getContent(),
                  context.getLocale());
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

      final Action viewReceipt =
          viewReceipt(
              context.getLocale(),
              congratsRequest.getSiteId(),
              congratsRequest.getPaymentMehotdsIds());

      final Text ifpeCompliance =
          textIfpeCompliance(
              congratsRequest.isIfpe(),
              congratsRequest.getPaymentMehotdsIds(),
              context.getLocale());

      return new Congrats(points, discounts, crossSelling, viewReceipt, ifpeCompliance);
    } catch (Exception e) {
      METRIC_COLLECTOR.incrementCounter(CONGRATS_ERROR_BUILD_CONGRATS);
      LOGGER.error(
          LogUtils.getServiceExceptionLog(
              context, "Congrats Service", congratsRequest.toString(), e));
      return new Congrats();
    }
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

    if (ifpeUtils.isIfpeEnabled(siteId) && validateAccountMoneyId(paymentMethodsIds)) {
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
}
