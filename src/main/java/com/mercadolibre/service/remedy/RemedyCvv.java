package com.mercadolibre.service.remedy;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDY_CVV_COUNTER;
import static com.mercadolibre.utils.Translations.*;

import com.mercadolibre.dto.remedy.FieldSetting;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseCvv;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.dto.Version;
import com.mercadolibre.px.toolkit.dto.user_agent.OperatingSystem;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;

public class RemedyCvv implements RemedyInterface {

  private final String FIELD_SETTING_NAME = "security_code";
  private static final String SECURITY_CODE_LOCATION_FRONT = "front";
  /** Hack Android Invalid version since */
  private static final Version INVALID_VERSION_ANDROID_SINCE = new Version("4.48.0");
  /** Hack Android Invalid version to */
  private static final Version INVALID_VERSION_ANDROID_TO = new Version("4.49.0");

  @Override
  public RemediesResponse applyRemedy(
      final Context context,
      final RemediesRequest remediesRequest,
      final RemediesResponse remediesResponse) {

    final PayerPaymentMethodRejected payerPaymentMethodRejected =
        remediesRequest.getPayerPaymentMethodRejected();

    if (payerPaymentMethodRejected == null
        || validateVersionAndroid(remediesRequest.getUserAgent())) {
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

  private boolean validateVersionAndroid(final UserAgent userAgent) {

    if (userAgent.getOperatingSystem().equals(OperatingSystem.ANDROID)
        && userAgent.getVersion().compareTo(INVALID_VERSION_ANDROID_SINCE) >= 0
        && userAgent.getVersion().compareTo(INVALID_VERSION_ANDROID_TO) < 0) {
      return true;
    }
    return false;
  }
}
