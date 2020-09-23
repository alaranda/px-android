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

import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.PaymentMethodSelected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseCvv;
import com.mercadolibre.dto.remedy.SuggestionPaymentMethodResponse;
import com.mercadolibre.dto.tracking.TrackingData;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.service.remedy.order.PaymentMethodsRejectedTypes;
import com.mercadolibre.service.remedy.order.SuggestionCriteriaInterface;
import com.mercadolibre.utils.SuggestionPaymentMehodsUtils;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;

public class RemedySuggestionPaymentMethod implements RemedyInterface {

  private RemedyCvv remedyCvv;
  private String remedyTitle;
  private String remedyMessage;
  private SuggestionPaymentMehodsUtils suggestionPaymentMehodsUtils;
  private PaymentMethodsRejectedTypes paymentMethodsRejectedTypes;

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

    final List<AlternativePayerPaymentMethod> consumerCredits =
        alternativePayerPaymentMethodList.stream()
            .filter(isConsumerCredits)
            .collect(Collectors.toList());

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

      final String title =
          String.format(
              Translations.INSTANCE.getTranslationByLocale(context.getLocale(), remedyTitle),
              payerPaymentMethodRejected.getIssuerName(),
              payerPaymentMethodRejected.getLastFourDigit());

      final String message =
          String.format(
              Translations.INSTANCE.getTranslationByLocale(context.getLocale(), remedyMessage),
              payerPaymentMethodRejected.getIssuerName());

      final SuggestionPaymentMethodResponse suggestionPaymentMethodResponse =
          SuggestionPaymentMethodResponse.builder()
              .title(title)
              .message(message)
              .alternativePaymentMethod(paymentMethodSelected.getAlternativePayerPaymentMethod())
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

  private static boolean cvvRequired(final PaymentMethodSelected paymentMethodSelected) {

    if (null != paymentMethodSelected.getAlternativePayerPaymentMethod()
        && paymentMethodSelected.isRemedyCvvRequired()) {

      return true;
    }

    return false;
  }
}
