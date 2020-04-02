package com.mercadolibre.service.remedy;

import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseCallForAuth;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.RemediesTexts;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDIES_COUNTER;

public class RemedyCallForAuthorize  implements RemedyInterface {

    private RemediesTexts remediesTexts;

    private final static String REMEDY_CALL_FOR_AUTH_TITLE = "remedy.callforauth.title";
    private final static String REMEDY_CALL_FOR_AUTH_MESSAGE = "remedy.callforauth.message";

    public RemedyCallForAuthorize(final RemediesTexts remediesTexts) {
        this.remediesTexts = remediesTexts;
    }

    @Override
    public RemediesResponse applyRemedy(final Context context, final RemediesRequest remediesRequest, final RemediesResponse remediesResponse) {

        final PayerPaymentMethodRejected payerPaymentMethodRejected = remediesRequest.getPayerPaymentMethodRejected();

        final String title = String.format(remediesTexts.getTranslation(context.getLocale(), REMEDY_CALL_FOR_AUTH_TITLE), payerPaymentMethodRejected.getPaymentMethodId(),
                payerPaymentMethodRejected.getIssuerName(), payerPaymentMethodRejected.getLastFourDigit());

        final String message = String.format(remediesTexts.getTranslation(context.getLocale(), REMEDY_CALL_FOR_AUTH_MESSAGE),
                payerPaymentMethodRejected.getIssuerName(),  payerPaymentMethodRejected.getTotalAmount());

        final ResponseCallForAuth responseCallForAuth = ResponseCallForAuth.builder()
                .title(title)
                .message(message)
                .build();

        remediesResponse.setCallForAuth(responseCallForAuth);

        DatadogRemediesMetrics.trackRemediesInfo(REMEDIES_COUNTER, context, remediesRequest);

        return remediesResponse;
    }
}
