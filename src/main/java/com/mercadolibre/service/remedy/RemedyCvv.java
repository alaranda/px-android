package com.mercadolibre.service.remedy;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDY_CVV_COUNTER;
import static com.mercadolibre.utils.Translations.*;

import com.mercadolibre.dto.remedy.FieldSetting;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseCvv;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;

public class RemedyCvv implements RemedyInterface {

  private final String FIELD_SETTING_NAME = "security_code";
  private static final String SECURITY_CODE_LOCATION_FRONT = "front";

  @Override
  public RemediesResponse applyRemedy(
      final Context context,
      final RemediesRequest remediesRequest,
      final RemediesResponse remediesResponse) {

    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        remediesRequest.getPayerPaymentMethodRejected();

    if (payerPaymentMethodRejected == null) {
      return remediesResponse;
    }

    final ResponseCvv responseCvv =
        buildRemedyCvv(
            context,
            remediesRequest.getPayerPaymentMethodRejected(),
            REMEDY_CVV_TITLE,
            REMEDY_CVV_MESSAGE);

    remediesResponse.setCvv(responseCvv);

    DatadogRemediesMetrics.trackRemediesInfo(REMEDY_CVV_COUNTER, context, remediesRequest);

    return remediesResponse;
  }

  public ResponseCvv buildRemedyCvv(
      final Context context,
      final PayerPaymentMethodRejected payerPaymentMethodRejected,
      final String remedyTitleKey,
      final String remedyMessageKey) {

    final String title =
        Translations.INSTANCE.getTranslationByLocale(context.getLocale(), remedyTitleKey);

    final String message =
        String.format(
            Translations.INSTANCE.getTranslationByLocale(context.getLocale(), remedyMessageKey),
            payerPaymentMethodRejected.getIssuerName(),
            payerPaymentMethodRejected.getLastFourDigit());

    final FieldSetting.FieldSettingBuilder fieldSettingBuilder =
        FieldSetting.builder()
            .name(FIELD_SETTING_NAME)
            .title(
                Translations.INSTANCE.getTranslationByLocale(
                    context.getLocale(), REMEDY_FIELD_SETTING_CVV_TITLE))
            .length(payerPaymentMethodRejected.getSecurityCodeLength());

    if (SECURITY_CODE_LOCATION_FRONT.equalsIgnoreCase(
        payerPaymentMethodRejected.getSecurityCodeLocation())) {
      fieldSettingBuilder.hintMessage(
          Translations.INSTANCE.getTranslationByLocale(
              context.getLocale(), REMEDY_FIELD_SETTING_CVV_HINT_MESSAGE_FRONT));
    } else {
      fieldSettingBuilder.hintMessage(
          Translations.INSTANCE.getTranslationByLocale(
              context.getLocale(), REMEDY_FIELD_SETTING_CVV_HINT_MESSAGE_BACK));
    }

    return ResponseCvv.builder()
        .title(title)
        .message(message)
        .fieldSetting(fieldSettingBuilder.build())
        .build();
  }
}
