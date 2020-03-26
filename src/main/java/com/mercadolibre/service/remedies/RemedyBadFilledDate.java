package com.mercadolibre.service.remedies;

import com.mercadolibre.dto.remedies.FieldSetting;
import com.mercadolibre.dto.remedies.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedies.RemediesRequest;
import com.mercadolibre.dto.remedies.RemediesResponse;
import com.mercadolibre.dto.remedies.ResponseBadFilledDate;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.RemediesTexts;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDIES_COUNTER;

public class RemedyBadFilledDate implements RemedyInterface {

    private RemediesTexts remediesTexts;

    private final String KEY_TITLE = "badFilledDate.title";
    private final String KEY_MESSAGE = "badFilledDate.message";
    private final String KEY_FIELD_SETTING_TITLE ="fieldSetting.date.title";
    private final String KEY_FIELD_SETTING_HINT_MESSAGE ="fieldSetting.date.hintMessage";

    private final String FIELD_SETTING_NAME = "expiration";

    public RemedyBadFilledDate(final RemediesTexts remediesTexts) {
        this.remediesTexts = remediesTexts;
    }

    @Override
    public RemediesResponse applyRemedy(final Context context, final RemediesRequest remediesRequest, final RemediesResponse remediesResponse) {

        final PayerPaymentMethodRejected payerPaymentMethodRejected = remediesRequest.getPayerPaymentMethodRejected();

        final String title = remediesTexts.getTranslation(context.getLocale(), KEY_TITLE);

        final String message = String.format(remediesTexts.getTranslation(context.getLocale(), KEY_MESSAGE),
                payerPaymentMethodRejected.getIssuerName(), payerPaymentMethodRejected.getLastFourDigit());

        final FieldSetting fieldSetting = FieldSetting.builder()
                .name(FIELD_SETTING_NAME)
                .title(remediesTexts.getTranslation(context.getLocale(), KEY_FIELD_SETTING_TITLE))
                .validationMessage(null)
                .hintMessage(remediesTexts.getTranslation(context.getLocale(), KEY_FIELD_SETTING_HINT_MESSAGE))
                .build();

        final ResponseBadFilledDate responseBadFilledDate = ResponseBadFilledDate.builder()
                .title(title)
                .message(message)
                .fieldSetting(fieldSetting).build();

        remediesResponse.setBadFilledDate(responseBadFilledDate);

        DatadogRemediesMetrics.trackRemediesInfo(REMEDIES_COUNTER, context, remediesRequest);

        return remediesResponse;
    }
}
