package com.mercadolibre.service.remedy;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDY_CALL_FOR_AUTHORIZE_COUNTER;
import static com.mercadolibre.utils.Translations.REMEDY_CALL_FOR_AUTHORIZE_BUTTON_LOUD;

import com.mercadolibre.dto.congrats.Action;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseCallForAuth;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RemedyCallForAuthorize implements RemedyInterface {

  private static final Logger LOGGER = LogManager.getLogger();

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

    final String title =
        String.format(
            Translations.INSTANCE.getTranslationByLocale(
                context.getLocale(), Translations.REMEDY_CALL_FOR_AUTHORIZE_TITLE),
            payerPaymentMethodRejected.getPaymentMethodId(),
            payerPaymentMethodRejected.getIssuerName(),
            payerPaymentMethodRejected.getLastFourDigit());

    final String message =
        String.format(
            Translations.INSTANCE.getTranslationByLocale(
                context.getLocale(), Translations.REMEDY_CALL_FOR_AUTHORIZE_MESSAGE),
            payerPaymentMethodRejected.getIssuerName(),
            payerPaymentMethodRejected.getTotalAmount());

    final ResponseCallForAuth responseCallForAuth =
        ResponseCallForAuth.builder()
            .title(title)
            .message(message)
            .actionLoud(
                new Action(
                    Translations.INSTANCE.getTranslationByLocale(
                        context.getLocale(), REMEDY_CALL_FOR_AUTHORIZE_BUTTON_LOUD)))
            .build();

    remediesResponse.setCallForAuth(responseCallForAuth);

    DatadogRemediesMetrics.trackRemediesInfo(
        REMEDY_CALL_FOR_AUTHORIZE_COUNTER, context, remediesRequest);

    return remediesResponse;
  }
}
