package com.mercadolibre.service.remedy;

import com.mercadolibre.api.RiskApi;
import com.mercadolibre.dto.congrats.Action;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.dto.remedy.ResponseHighRisk;
import com.mercadolibre.dto.risk.RiskResponse;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.site.Site;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.utils.RemediesTexts;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.utils.StringUtils;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDIES_COUNTER;
import static com.mercadolibre.constants.DatadogMetricsNames.REMEDIES_KYC_FAIL_COUNTER;
import static com.mercadolibre.utils.Translations.REMEDY_HIGH_RISK_BUTTON_LOUD;
import static com.mercadolibre.utils.Translations.REMEDY_HIGH_RISK_MESSAGE;
import static com.mercadolibre.utils.Translations.REMEDY_HIGH_RISK_TITLE;

public class RemedyHighRisk implements RemedyInterface {

    private static final Logger LOGGER = LogManager.getLogger();
    private RemediesTexts remediesTexts;
    private RiskApi riskApi;

    private final static String KYC_REMEDY = "available_for_remedy";
    private static final String KYC_DEEPLINK = "%s://kyc/?initiative=px-high-risk&callback=%s://example-callback/";

    public RemedyHighRisk(final RemediesTexts remediesTexts, final RiskApi riskApi) {
        this.remediesTexts = remediesTexts;
        this.riskApi = riskApi;
    }

    @Override
    public RemediesResponse applyRemedy(final Context context, final RemediesRequest remediesRequest,
                                        final RemediesResponse remediesResponse) {
        try {

            if (StringUtils.isBlank(remediesRequest.getUserId())) {
                LOGGER.info("Invalid userId for Kyc Remedy");
                return null;
            }

            final PayerPaymentMethodRejected payerPaymentMethodRejected = remediesRequest.getPayerPaymentMethodRejected();

            if (!remediesRequest.getSiteId().equalsIgnoreCase(Site.MLA.name()) && !remediesRequest.getSiteId().equalsIgnoreCase(Site.MLB.name())){
                LOGGER.info("Invalid site for Kyc Remedy");
                return null;
            }

            final RiskResponse riskResponse =  riskApi.getRisk(context, remediesRequest.getRiskExcecutionId());

            if (remedyKyc(context, riskResponse)) {

                final String title = Translations.INSTANCE.getTranslationByLocale(context.getLocale(), REMEDY_HIGH_RISK_TITLE);

                final String message = Translations.INSTANCE.getTranslationByLocale(context.getLocale(), REMEDY_HIGH_RISK_MESSAGE);

                final String deppLink = String.format(KYC_DEEPLINK, context.getPlatform().getName().toLowerCase(), context.getPlatform().getName().toLowerCase());

                final ResponseHighRisk responseHighRisk = ResponseHighRisk.builder()
                        .title(title)
                        .message(message)
                        .deepLink(deppLink)
                        .actionLoud(new Action(Translations.INSTANCE.getTranslationByLocale(context.getLocale(), REMEDY_HIGH_RISK_BUTTON_LOUD)))
                        .build();

                remediesResponse.setHighRisk(responseHighRisk);

                DatadogRemediesMetrics.trackRemediesInfo(REMEDIES_COUNTER, context, remediesRequest);
            }

            return remediesResponse;

        } catch (ApiException e) {
            LOGGER.info("Invalid risk for Kyc Remedy");
            DatadogRemediesMetrics.trackRemediesInfo(REMEDIES_KYC_FAIL_COUNTER, context, remediesRequest);
        }

        return null;
    }

    private boolean remedyKyc(final Context context, final RiskResponse riskResponse) {

        final String tags = riskResponse.getTags();

        if (StringUtils.isNotBlank(tags) && tags.contains(KYC_REMEDY) && context.getPlatform() != null){
            return true;
        }

       return false;
    }

}
