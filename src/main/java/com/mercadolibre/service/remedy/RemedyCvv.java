package com.mercadolibre.service.remedy;

import com.mercadolibre.dto.remedy.FieldSetting;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseCvv;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDIES_COUNTER;
import static com.mercadolibre.utils.Translations.*;

public class RemedyCvv implements RemedyInterface {

    private final String FIELD_SETTING_NAME = "security_code";
    private static final String LOCATION_BACK = "back";

    @Override
    public RemediesResponse applyRemedy(final Context context, final RemediesRequest remediesRequest, final RemediesResponse remediesResponse) {

        final PayerPaymentMethodRejected payerPaymentMethodRejected = remediesRequest.getPayerPaymentMethodRejected();

        final String title = Translations.INSTANCE.getTranslationByLocale(context.getLocale(), REMEDY_CVV_TITLE);

        final String message = Translations.INSTANCE.getTranslationByLocale(context.getLocale(), REMEDY_CVV_MESSAGE);

        final FieldSetting.FieldSettingBuilder fieldSettingBuilder = FieldSetting.builder()
                .name(FIELD_SETTING_NAME)
                .title(Translations.INSTANCE.getTranslationByLocale(context.getLocale(), REMEDY_FIELD_SETTING_CVV_TITLE))
                .length(payerPaymentMethodRejected.getSecurityCodeLength());

        if (!payerPaymentMethodRejected.getSecurityCodeLocation().equalsIgnoreCase(LOCATION_BACK)){
            fieldSettingBuilder.hintMessage(Translations.INSTANCE.getTranslationByLocale(context.getLocale(), REMEDY_FIELD_SETTING_CVV_HINT_MESSAGE_FRONT));
        } else {
            fieldSettingBuilder.hintMessage(Translations.INSTANCE.getTranslationByLocale(context.getLocale(), REMEDY_FIELD_SETTING_CVV_HINT_MESSAGE_BACK));
        }

        final ResponseCvv responseCvv = ResponseCvv.builder()
                .title(title)
                .message(message)
                .fieldSetting(fieldSettingBuilder.build())
                .build();

        remediesResponse.setCvv(responseCvv);

        DatadogRemediesMetrics.trackRemediesInfo(REMEDIES_COUNTER, context, remediesRequest);

        return remediesResponse;
    }
}
