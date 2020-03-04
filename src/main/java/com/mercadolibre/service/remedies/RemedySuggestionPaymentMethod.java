package com.mercadolibre.service.remedies;

import com.mercadolibre.dto.remedies.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedies.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedies.RemediesRequest;
import com.mercadolibre.dto.remedies.RemediesResponse;
import com.mercadolibre.dto.remedies.SuggestionPaymentMethodResponse;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.RemediesTexts;
import com.mercadolibre.utils.SuggestionPaymentMehodsUtils;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDIES_ALTERNATIVE_PAYMENT_METHOD;
import static com.mercadolibre.constants.DatadogMetricsNames.REMEDIES_ALTERNATIVE_PAYMENT_METHOD_ESC;
import static com.mercadolibre.constants.DatadogMetricsNames.REMEDIES_COUNTER;


public class RemedySuggestionPaymentMethod  implements RemedyInterface {

    private RemediesTexts remediesTexts;
    private String statusDetail;

    public RemedySuggestionPaymentMethod(final RemediesTexts remediesTexts, final String statusDetail) {
        this.remediesTexts = remediesTexts;
        this.statusDetail = statusDetail;
    }

    @Override
    public RemediesResponse applyRemedy(final Context context, final RemediesRequest remediesRequest, final RemediesResponse remediesResponse) {

        final PayerPaymentMethodRejected payerPaymentMethodRejected = remediesRequest.getPayerPaymentMethodRejected();

        final AlternativePayerPaymentMethod alternativePayerPaymentMethod = SuggestionPaymentMehodsUtils.findPaymentMethodEqualsAmount(remediesRequest);

        if (null != alternativePayerPaymentMethod) {

            final String title = String.format(remediesTexts.getTranslation(context.getLocale(), statusDetail.concat(".title")),
                    payerPaymentMethodRejected.getPaymentMethodId(), payerPaymentMethodRejected.getIssuerName(), payerPaymentMethodRejected.getLastFourDigit());

            final String message = String.format(remediesTexts.getTranslation(context.getLocale(), statusDetail.concat(".message")),
                    payerPaymentMethodRejected.getPaymentMethodId(), payerPaymentMethodRejected.getIssuerName(), payerPaymentMethodRejected.getLastFourDigit());

            final SuggestionPaymentMethodResponse suggestionPaymentMethodResponse = new SuggestionPaymentMethodResponse(title, message, alternativePayerPaymentMethod);

            DatadogRemediesMetrics.trackRemediesInfo(REMEDIES_ALTERNATIVE_PAYMENT_METHOD, context, remediesRequest);

            if (alternativePayerPaymentMethod.isEsc()){
                DatadogRemediesMetrics.trackRemediesInfo(REMEDIES_ALTERNATIVE_PAYMENT_METHOD_ESC, context, remediesRequest);
            }

            remediesResponse.setSuggestionPaymentMethod(suggestionPaymentMethodResponse);
        }

        DatadogRemediesMetrics.trackRemediesInfo(REMEDIES_COUNTER, context, remediesRequest);

        return remediesResponse;
    }
}
