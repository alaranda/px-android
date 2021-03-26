package com.mercadolibre.service.remedy;

import static com.mercadolibre.constants.Constants.STATUS_APPROVED;
import static com.mercadolibre.constants.DatadogMetricsNames.REMEDY_SILVER_BULLET;
import static com.mercadolibre.constants.DatadogMetricsNames.REMEDY_SILVER_BULLET_INTENT;
import static com.mercadolibre.constants.DatadogMetricsNames.SILVER_BULLET_WITHOUT_PM;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.ACCOUNT_MONEY;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.CONSUMER_CREDITS;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.CREDIT_CARD;
import static com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes.DEBIT_CARD;
import static com.mercadolibre.utils.Translations.REMEDY_CVV_SUGGESTION_PM_MESSAGE;
import static com.mercadolibre.utils.Translations.REMEDY_CVV_TITLE;
import static com.mercadolibre.utils.Translations.REMEDY_GENERIC_TITLE;
import static com.mercadolibre.utils.Translations.TOTAL_PAY_GENERIC_LABEL;
import static com.mercadolibre.utils.Translations.WITH_ACCOUNT_MONEY_GENERIC_LABEL;
import static com.mercadolibre.utils.Translations.WITH_CREDIT_GENERIC_LABEL;
import static com.mercadolibre.utils.Translations.WITH_DEBIT_GENERIC_LABEL;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.CustomStringConfiguration;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.PaymentMethodSelected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseCvv;
import com.mercadolibre.dto.remedy.SuggestionPaymentMethodResponse;
import com.mercadolibre.dto.tracking.TrackingData;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.context.OperatingSystem;
import com.mercadolibre.px.dto.lib.context.UserAgent;
import com.mercadolibre.px.dto.lib.context.Version;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.dto.lib.text.Text;
import com.mercadolibre.px_config.Config;
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

  private static final List<String> hybridCardBins =
      Arrays.asList(StringUtils.split(Config.getString("hybrid.cards.bins"), "|"));

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

    if (!Site.MLM.getSiteId().equals(remediesRequest.getSiteId())
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

      final AlternativePayerPaymentMethod alternativePayerPaymentMethod =
          paymentMethodSelected.getAlternativePayerPaymentMethod();

      final Text text =
          buildText(
              context.getLocale(),
              remediesRequest.getCustomStringConfiguration(),
              remediesRequest,
              alternativePayerPaymentMethod);

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
      Locale locale,
      CustomStringConfiguration customStringConfiguration,
      RemediesRequest remediesRequest,
      AlternativePayerPaymentMethod alternativePayerPaymentMethod) {

    String message = Translations.INSTANCE.getTranslationByLocale(locale, TOTAL_PAY_GENERIC_LABEL);

    String changeableTypeId = "";

    String suffix = "";

    final boolean isAMPresentInRequest =
        remediesRequest.getAlternativePayerPaymentMethods().stream()
                .anyMatch(pm -> ACCOUNT_MONEY.equalsIgnoreCase(pm.getPaymentTypeId()))
            || remediesRequest
                .getPayerPaymentMethodRejected()
                .getPaymentTypeId()
                .equalsIgnoreCase(ACCOUNT_MONEY);

    if (isAMPresentInRequest && hybridCardBins.contains(alternativePayerPaymentMethod.getBin())) {
      changeableTypeId = CREDIT_CARD;
    }

    boolean isCardHybridInRejected =
        (remediesRequest
                    .getPayerPaymentMethodRejected()
                    .getPaymentTypeId()
                    .equalsIgnoreCase(CREDIT_CARD)
                || remediesRequest
                    .getPayerPaymentMethodRejected()
                    .getPaymentTypeId()
                    .equalsIgnoreCase(DEBIT_CARD))
            && hybridCardBins.contains(remediesRequest.getPayerPaymentMethodRejected().getBin());

    boolean isCardHybridBinPresent =
        remediesRequest.getAlternativePayerPaymentMethods().stream()
            .anyMatch(
                pm ->
                    (CREDIT_CARD.equalsIgnoreCase(pm.getPaymentTypeId())
                            || DEBIT_CARD.equalsIgnoreCase(pm.getPaymentTypeId()))
                        && hybridCardBins.contains(pm.getBin()));

    if ((isCardHybridInRejected || isCardHybridBinPresent)
        && ACCOUNT_MONEY.equalsIgnoreCase(alternativePayerPaymentMethod.getPaymentTypeId())) {
      changeableTypeId = ACCOUNT_MONEY;
    }

    if (isOfferingComboCard(remediesRequest, alternativePayerPaymentMethod)) {
      changeableTypeId = alternativePayerPaymentMethod.getPaymentTypeId().toLowerCase();
    }

    switch (changeableTypeId) {
      case DEBIT_CARD:
        suffix = Translations.INSTANCE.getTranslationByLocale(locale, WITH_DEBIT_GENERIC_LABEL);
        message = String.format("%s %s", message, suffix);
        break;
      case ACCOUNT_MONEY:
        suffix =
            Translations.INSTANCE.getTranslationByLocale(locale, WITH_ACCOUNT_MONEY_GENERIC_LABEL);
        message = String.format("%s %s", message, suffix);
        break;
      case CREDIT_CARD:
        suffix = Translations.INSTANCE.getTranslationByLocale(locale, WITH_CREDIT_GENERIC_LABEL);
        message = String.format("%s %s", message, suffix);
        break;
    }

    if (customStringConfiguration != null
        && StringUtils.isNotBlank(customStringConfiguration.getTotalDescriptionText())) {
      message = StringUtils.trim(customStringConfiguration.getTotalDescriptionText());
      if (StringUtils.isNotBlank(suffix)) {
        message = String.format("%s %s", message, suffix);
      }
    }

    return new Text(
        message, Constants.WHITE_COLOR, Constants.BLACK_COLOR, Constants.WEIGHT_SEMI_BOLD);
  }

  private static boolean isOfferingComboCard(
      RemediesRequest remediesRequest,
      AlternativePayerPaymentMethod alternativePayerPaymentMethod) {

    if (remediesRequest
        .getPayerPaymentMethodRejected()
        .getCustomOptionId()
        .equals(alternativePayerPaymentMethod.getCustomOptionId())) {
      return Boolean.TRUE;
    }

    List<String> idCards =
        remediesRequest.getAlternativePayerPaymentMethods().stream()
            .map(AlternativePayerPaymentMethod::getCustomOptionId)
            .filter(
                customOptionId ->
                    remediesRequest.getAlternativePayerPaymentMethods().stream()
                            .filter(_apm -> _apm.getCustomOptionId().equals(customOptionId))
                            .count()
                        > 1)
            .collect(Collectors.toList());

    return idCards.contains(alternativePayerPaymentMethod.getCustomOptionId());
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
