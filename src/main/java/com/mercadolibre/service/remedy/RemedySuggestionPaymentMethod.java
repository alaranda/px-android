package com.mercadolibre.service.remedy;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDY_SILVER_BULLET;
import static com.mercadolibre.constants.DatadogMetricsNames.REMEDY_SILVER_BULLET_INTENT;
import static com.mercadolibre.constants.DatadogMetricsNames.SILVER_BULLET_WITHOUT_PM;
import static com.mercadolibre.utils.Translations.REMEDY_CVV_SUGGESTION_PM_MESSAGE;
import static com.mercadolibre.utils.Translations.REMEDY_CVV_TITLE;

import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.PaymentMethodSelected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseCvv;
import com.mercadolibre.dto.remedy.SuggestionPaymentMethodResponse;
import com.mercadolibre.dto.tracking.TrackingData;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.SuggestionPaymentMehodsUtils;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;

public class RemedySuggestionPaymentMethod implements RemedyInterface {

  private RemedyCvv remedyCvv;
  private String remedyTitle;
  private String remedyMessage;
  private SuggestionPaymentMehodsUtils suggestionPaymentMehodsUtils;

  public RemedySuggestionPaymentMethod(
      final RemedyCvv remedyCvv, final String remedyTitle, final String remedyMessage) {
    this.remedyCvv = remedyCvv;
    this.remedyTitle = remedyTitle;
    this.remedyMessage = remedyMessage;
    this.suggestionPaymentMehodsUtils = new SuggestionPaymentMehodsUtils();
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

    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        remediesRequest.getPayerPaymentMethodRejected();

    final PaymentMethodSelected paymentMethodSelected =
        suggestionPaymentMehodsUtils.findPaymentMethodSuggestionsAmount(remediesRequest);

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
