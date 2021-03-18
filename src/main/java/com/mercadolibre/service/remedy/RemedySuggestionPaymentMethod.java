package com.mercadolibre.service.remedy;

import static com.mercadolibre.constants.Constants.STATUS_APPROVED;
import static com.mercadolibre.constants.DatadogMetricsNames.*;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.*;
import static com.mercadolibre.utils.Translations.*;

import com.mercadolibre.dto.remedy.*;
import com.mercadolibre.dto.tracking.TrackingData;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.context.OperatingSystem;
import com.mercadolibre.px.dto.lib.context.UserAgent;
import com.mercadolibre.px.dto.lib.context.Version;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes;
import com.mercadolibre.service.remedy.order.SuggestionCriteriaInterface;
import com.mercadolibre.utils.SuggestionPaymentMehodsUtils;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class RemedySuggestionPaymentMethod implements RemedyInterface {

  private final RemedyCvv remedyCvv;
  private final String remedyTitle;
  private final String remedyMessage;
  private final SuggestionPaymentMehodsUtils suggestionPaymentMehodsUtils;
  private final PaymentMethodsRejectedTypes paymentMethodsRejectedTypes;

  /** Hack IOS valid version since */
  private static final Version CREDITS_VALID_VERSION_IOS_SINCE = new Version("4.36.4");

  private final Predicate<AlternativePayerPaymentMethod> isDebitCard =
      e -> e.getPaymentTypeId().equalsIgnoreCase(DEBIT_CARD);
  private final Predicate<AlternativePayerPaymentMethod> isCreditCard =
      e -> e.getPaymentTypeId().equalsIgnoreCase(CREDIT_CARD);
  private final Predicate<AlternativePayerPaymentMethod> isAccountMoney =
      e -> e.getPaymentTypeId().equalsIgnoreCase(ACCOUNT_MONEY);
  private final Predicate<AlternativePayerPaymentMethod> isConsumerCredits =
      e -> e.getPaymentTypeId().equalsIgnoreCase(CONSUMER_CREDITS);
  private final Predicate<AlternativePayerPaymentMethod> containEsc =
      e -> e.getEscStatus().equalsIgnoreCase(STATUS_APPROVED);

  public static final String DEBIT_CARD_ESC = "debit_card_esc";
  public static final String DEBIT_CARD_WITHOUT_ESC = "debit_card_without_esc";
  public static final String CREDIT_CARD_ESC = "credit_card_esc";
  public static final String CREDIT_CARD_WITHOUT_ESC = "credit_card_without_esc";

  public RemedySuggestionPaymentMethod(
      final RemedyCvv remedyCvv, final String remedyTitle, final String remedyMessage) {
    this.remedyCvv = remedyCvv;
    this.remedyTitle = remedyTitle;
    this.remedyMessage = remedyMessage;
    this.suggestionPaymentMehodsUtils = new SuggestionPaymentMehodsUtils();
    this.paymentMethodsRejectedTypes = new PaymentMethodsRejectedTypes();
  }

  @Override
  public RemediesResponse applyRemedy(
      final Context context,
      final RemediesRequest remediesRequest,
      final RemediesResponse remediesResponse) {

    DatadogRemediesMetrics.trackRemediesInfo(REMEDY_SILVER_BULLET_INTENT, context, remediesRequest);

    if (!remediesRequest.isOneTap()) {
      return remediesResponse;
    }

    final List<AlternativePayerPaymentMethod> alternativePayerPaymentMethodList =
        remediesRequest.getAlternativePayerPaymentMethods();

    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        remediesRequest.getPayerPaymentMethodRejected();

    if (CollectionUtils.isEmpty(alternativePayerPaymentMethodList)
        || null == payerPaymentMethodRejected) {
      DatadogRemediesMetrics.trackRemediesInfo(SILVER_BULLET_WITHOUT_PM, context, remediesRequest);
      return remediesResponse;
    }

    final List<AlternativePayerPaymentMethod> accountMoney =
        alternativePayerPaymentMethodList.stream()
            .filter(isAccountMoney)
            .collect(Collectors.toList());

    List<AlternativePayerPaymentMethod> consumerCredits = new ArrayList<>();

    boolean isMLMSite = Site.MLM.getSiteId().equals(remediesRequest.getSiteId());
    if (!isMLMSite
        && (iosVersionIsValidForCredits(context.getUserAgent())
            || OperatingSystem.isAndroid(context.getUserAgent().getOperatingSystem()))) {
      consumerCredits =
          alternativePayerPaymentMethodList.stream()
              .filter(isConsumerCredits)
              .collect(Collectors.toList());
    }

    final List<AlternativePayerPaymentMethod> debitCardEsc =
        alternativePayerPaymentMethodList.stream()
            .filter(isDebitCard.and(containEsc))
            .collect(Collectors.toList());

    final List<AlternativePayerPaymentMethod> debitCardWithOutEsc =
        alternativePayerPaymentMethodList.stream()
            .filter(isDebitCard.and(containEsc.negate()))
            .collect(Collectors.toList());

    final List<AlternativePayerPaymentMethod> creditCardEsc =
        alternativePayerPaymentMethodList.stream()
            .filter(isCreditCard.and(containEsc))
            .collect(Collectors.toList());

    final List<AlternativePayerPaymentMethod> creditCardWithOutEsc =
        alternativePayerPaymentMethodList.stream()
            .filter(isCreditCard.and(containEsc.negate()))
            .collect(Collectors.toList());

    Map<String, List<AlternativePayerPaymentMethod>> payerPaymentMethodsMap = new HashMap<>();
    payerPaymentMethodsMap.put(ACCOUNT_MONEY, accountMoney);
    payerPaymentMethodsMap.put(CONSUMER_CREDITS, consumerCredits);
    payerPaymentMethodsMap.put(DEBIT_CARD_ESC, debitCardEsc);
    payerPaymentMethodsMap.put(DEBIT_CARD_WITHOUT_ESC, debitCardWithOutEsc);
    payerPaymentMethodsMap.put(CREDIT_CARD_ESC, creditCardEsc);
    payerPaymentMethodsMap.put(CREDIT_CARD_WITHOUT_ESC, creditCardWithOutEsc);

    SuggestionCriteriaInterface suggestionCriteriaInterface =
        paymentMethodsRejectedTypes.getSuggestionOrderCriteria(
            payerPaymentMethodRejected.getPaymentTypeId());

    final PaymentMethodSelected paymentMethodSelected =
        suggestionCriteriaInterface.findBestMedium(remediesRequest, payerPaymentMethodsMap);

    if (null != paymentMethodSelected) {

      String title =
          String.format(
              Translations.INSTANCE.getTranslationByLocale(context.getLocale(), remedyTitle),
              payerPaymentMethodRejected.getIssuerName(),
              payerPaymentMethodRejected.getLastFourDigit());

      if (payerPaymentMethodRejected.getPaymentTypeId().equalsIgnoreCase(ACCOUNT_MONEY)
          || payerPaymentMethodRejected.getPaymentTypeId().equalsIgnoreCase(CONSUMER_CREDITS)) {

        title =
            Translations.INSTANCE.getTranslationByLocale(context.getLocale(), REMEDY_GENERIC_TITLE);
      }

      final String message =
          String.format(
              Translations.INSTANCE.getTranslationByLocale(context.getLocale(), remedyMessage),
              payerPaymentMethodRejected.getIssuerName());

      AlternativePayerPaymentMethod alternativePayerPaymentMethod =
          paymentMethodSelected.getAlternativePayerPaymentMethod();

      Text text =
          buildText(
              context.getLocale(),
              alternativePayerPaymentMethod.getPaymentTypeId(),
              remediesRequest.getCustomStringConfiguration());

      final SuggestionPaymentMethodResponse suggestionPaymentMethodResponse =
          SuggestionPaymentMethodResponse.builder()
              .title(title)
              .message(message)
              .alternativePaymentMethod(alternativePayerPaymentMethod)
              .bottomMessage(text)
              .build();

      boolean frictionless = true;
      if (cvvRequired(paymentMethodSelected)) {

        final PayerPaymentMethodRejected paymentMethodRejectedSelected =
            PayerPaymentMethodRejected.builder()
                .paymentMethodId(
                    paymentMethodSelected.getAlternativePayerPaymentMethod().getPaymentMethodId())
                .lastFourDigit(
                    paymentMethodSelected.getAlternativePayerPaymentMethod().getLastFourDigit())
                .securityCodeLength(
                    paymentMethodSelected
                        .getAlternativePayerPaymentMethod()
                        .getSecurityCodeLength())
                .securityCodeLocation(
                    paymentMethodSelected
                        .getAlternativePayerPaymentMethod()
                        .getSecurityCodeLocation())
                .issuerName(
                    paymentMethodSelected.getAlternativePayerPaymentMethod().getIssuerName())
                .build();
        final ResponseCvv responseCvv =
            remedyCvv.buildRemedyCvv(
                context,
                paymentMethodRejectedSelected,
                REMEDY_CVV_TITLE,
                REMEDY_CVV_SUGGESTION_PM_MESSAGE);

        remediesResponse.setCvv(responseCvv);

        frictionless = false;
      }

      final TrackingData trackingData =
          suggestionPaymentMehodsUtils.generateTrackingData(
              payerPaymentMethodRejected,
              suggestionPaymentMethodResponse.getAlternativePaymentMethod(),
              frictionless);

      DatadogRemediesMetrics.trackRemedySilverBulletInfo(
          REMEDY_SILVER_BULLET, context, remediesRequest, trackingData);

      remediesResponse.setTrackingData(trackingData);
      remediesResponse.setSuggestedPaymentMethod(suggestionPaymentMethodResponse);
      return remediesResponse;
    }

    DatadogRemediesMetrics.trackRemediesInfo(SILVER_BULLET_WITHOUT_PM, context, remediesRequest);

    return remediesResponse;
  }

  public Text buildText(
      Locale locale, String paymentMethodId, CustomStringConfiguration customStringConfiguration) {

    String message = Translations.INSTANCE.getTranslationByLocale(locale, TOTAL_PAY_GENERIC_LABEL);

    String suffix = "";

    if (PaymentMethodsRejectedTypes.DEBIT_CARD.equalsIgnoreCase(paymentMethodId)) {
      suffix = Translations.INSTANCE.getTranslationByLocale(locale, WITH_DEBIT_GENERIC_LABEL);
      message = String.format("%s %s", message, suffix);
    } else if (PaymentMethodsRejectedTypes.CREDIT_CARD.equalsIgnoreCase(paymentMethodId)) {
      suffix = Translations.INSTANCE.getTranslationByLocale(locale, WITH_CREDIT_GENERIC_LABEL);
      message = String.format("%s %s", message, suffix);
    }

    if (customStringConfiguration != null
        && StringUtils.isNotBlank(customStringConfiguration.getTotalDescriptionText())) {
      message = StringUtils.trim(customStringConfiguration.getTotalDescriptionText());
      if (StringUtils.isNotBlank(suffix)) {
        message = String.format("%s %s", message, suffix);
      }
    }

    Text text = new Text();
    text.setMessage(message);
    text.setTextColor("#000000");
    text.setWeight("bold");
    text.setBackgroundColor("#FFFFFF");

    return text;
  }

  private static boolean cvvRequired(final PaymentMethodSelected paymentMethodSelected) {
    return null != paymentMethodSelected.getAlternativePayerPaymentMethod()
        && paymentMethodSelected.isRemedyCvvRequired();
  }

  private boolean iosVersionIsValidForCredits(final UserAgent userAgent) {
    return OperatingSystem.isIOS(userAgent.getOperatingSystem())
        && userAgent.getVersion().compareTo(CREDITS_VALID_VERSION_IOS_SINCE) >= 0;
  }
}
