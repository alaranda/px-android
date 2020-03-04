package com.mercadolibre.service.remedies;

import com.mercadolibre.api.RiskApi;
import com.mercadolibre.dto.remedies.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedies.RemediesRequest;
import com.mercadolibre.dto.remedies.RemediesResponse;
import com.mercadolibre.dto.remedies.ResponseHighRisk;
import com.mercadolibre.dto.risk.RiskResponse;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.constants.Site;
import com.mercadolibre.utils.RemediesTexts;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;
import spark.utils.StringUtils;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDIES_COUNTER;

public class RemedyHighRisk implements RemedyInterface {

    private RemediesTexts remediesTexts;
    private RiskApi riskApi;

    private final String KEY_TITLE = "rejected_high_risk.title";
    private final String KEY_MESSAGE = "rejected_high_risk.message";
    private final static String KYC_REMEDY = "available_for_remedy";

    public RemedyHighRisk(final RemediesTexts remediesTexts, final RiskApi riskApi) {
        this.remediesTexts = remediesTexts;
        this.riskApi = riskApi;
    }

    @Override
    public RemediesResponse applyRemedy(final Context context, final RemediesRequest remediesRequest,
                                        final RemediesResponse remediesResponse) {
        try {

            if (StringUtils.isBlank(remediesRequest.getUserId())) {
                return null;
            }

            final PayerPaymentMethodRejected payerPaymentMethodRejected = remediesRequest.getPayerPaymentMethodRejected();

            if (!remediesRequest.getSiteId().equalsIgnoreCase(Site.MLA.getName()) && !remediesRequest.getSiteId().equalsIgnoreCase(Site.MLB.getName())){
                return null;
            }

            final RiskResponse riskResponse =  riskApi.getRisk(context, remediesRequest.getRiskExcecutionId());

            if (remedyKyc(riskResponse)) {

                final String title = String.format(remediesTexts.getTranslation(context.getLocale(), KEY_TITLE), payerPaymentMethodRejected.getPaymentMethodId(),
                        payerPaymentMethodRejected.getIssuerName(), payerPaymentMethodRejected.getLastFourDigit());

                final String message = String.format(remediesTexts.getTranslation(context.getLocale(), KEY_MESSAGE),
                        payerPaymentMethodRejected.getIssuerName(),  payerPaymentMethodRejected.getTotalAmount());

                final ResponseHighRisk responseHighRisk = new ResponseHighRisk(title, message);
                remediesResponse.setHighRisk(responseHighRisk);

                DatadogRemediesMetrics.trackRemediesInfo(REMEDIES_COUNTER, context, remediesRequest);
            }

            return remediesResponse;

        } catch (ApiException e) {

        }

        return null;
    }

    private boolean remedyKyc(final RiskResponse riskResponse) {

        final String tags = riskResponse.getTags();

        if (StringUtils.isNotBlank(tags) && tags.contains(KYC_REMEDY)){
            return true;
        }

       return false;
    }

}
