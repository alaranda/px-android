package com.mercadolibre.service.remedies;

import com.mercadolibre.dto.remedies.FieldSetting;
import com.mercadolibre.dto.remedies.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedies.RemediesRequest;
import com.mercadolibre.dto.remedies.RemediesResponse;
import com.mercadolibre.dto.remedies.ResponseCvv;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.RemediesTexts;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDIES_COUNTER;

public class RemedyCvv implements RemedyInterface {

    private RemediesTexts remediesTexts;

    private final String KEY_TITLE = "badCvv.title";
    private final String KEY_MESSAGE = "badCvv.message";
    private final String KEY_FIELD_SETTING_TITLE ="fieldSetting.cvv.title";
    private final String KEY_FIELD_SETTING_HINT_MESSAGE ="fieldSetting.cvv.hintMessage";

    private final String FIELD_SETTING_NAME = "security_code";

    public RemedyCvv(final RemediesTexts remediesTexts) {
        this.remediesTexts = remediesTexts;
    }

    @Override
    public RemediesResponse applyRemedy(final Context context, final RemediesRequest remediesRequest, final RemediesResponse remediesResponse) {

        final PayerPaymentMethodRejected payerPaymentMethodRejected = remediesRequest.getPayerPaymentMethodRejected();

        final String title = remediesTexts.getTranslation(context.getLocale(), KEY_TITLE);

        final String message = String.format(remediesTexts.getTranslation(context.getLocale(), KEY_MESSAGE),
                payerPaymentMethodRejected.getIssuerName(), payerPaymentMethodRejected.getLastFourDigit());

        final FieldSetting fieldSetting = new FieldSetting.Builder(FIELD_SETTING_NAME, remediesTexts.getTranslation(context.getLocale(), KEY_FIELD_SETTING_TITLE),
                null, String.format(remediesTexts.getTranslation(context.getLocale(), KEY_FIELD_SETTING_HINT_MESSAGE),
                payerPaymentMethodRejected.getSecurityCodeLength(), payerPaymentMethodRejected.getSecurityCodeLocation()))
                .withLength(payerPaymentMethodRejected.getSecurityCodeLength())
                .build();

        final ResponseCvv responseCvv = new ResponseCvv(title, message, fieldSetting);

        remediesResponse.setResponseCvv(responseCvv);

        DatadogRemediesMetrics.trackRemediesInfo(REMEDIES_COUNTER, context, remediesRequest);

        return remediesResponse;
    }
}
