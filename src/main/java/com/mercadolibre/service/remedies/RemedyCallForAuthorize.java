package com.mercadolibre.service.remedies;

import com.mercadolibre.dto.remedies.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedies.RemediesRequest;
import com.mercadolibre.dto.remedies.RemediesResponse;
import com.mercadolibre.dto.remedies.ResponseCallForAuth;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.RemediesTexts;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDIES_COUNTER;

public class RemedyCallForAuthorize  implements RemedyInterface {

    private RemediesTexts remediesTexts;

    private final String KEY_TITLE = "callForAuthorize.title";
    private final String KEY_MESSAGE = "callForAuthorize.message";

    public RemedyCallForAuthorize(final RemediesTexts remediesTexts) {
        this.remediesTexts = remediesTexts;
    }

    @Override
    public RemediesResponse applyRemedy(final Context context, final RemediesRequest remediesRequest, final RemediesResponse remediesResponse) {

        final PayerPaymentMethodRejected payerPaymentMethodRejected = remediesRequest.getPayerPaymentMethodRejected();

        final String title = String.format(remediesTexts.getTranslation(context.getLocale(), KEY_TITLE), payerPaymentMethodRejected.getPaymentMethodId(),
                payerPaymentMethodRejected.getIssuerName(), payerPaymentMethodRejected.getLastFourDigit());

        final String message = String.format(remediesTexts.getTranslation(context.getLocale(), KEY_MESSAGE),
                payerPaymentMethodRejected.getIssuerName(),  payerPaymentMethodRejected.getTotalAmount());

        final ResponseCallForAuth responseCallForAuth = new ResponseCallForAuth(title, message);

        remediesResponse.setResponseCallForAuth(responseCallForAuth);

        DatadogRemediesMetrics.trackRemediesInfo(REMEDIES_COUNTER, context, remediesRequest);

        return remediesResponse;
    }
}
