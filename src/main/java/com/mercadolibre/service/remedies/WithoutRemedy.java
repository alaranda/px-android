package com.mercadolibre.service.remedies;

import com.mercadolibre.dto.remedies.RemediesRequest;
import com.mercadolibre.dto.remedies.RemediesResponse;
import com.mercadolibre.dto.remedies.ResponseRemedyDefault;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.RemediesTexts;
import com.mercadolibre.utils.datadog.DatadogRemediesMetrics;

import static com.mercadolibre.constants.DatadogMetricsNames.REMEDIES_COUNTER;

public class WithoutRemedy implements RemedyInterface {

    private RemediesTexts remediesTexts;

    private final String KEY_TITLE = "defaultRemedy.title";
    private final String KEY_MESSAGE = "defaultRemedy.message";

    public WithoutRemedy(final RemediesTexts remediesTexts) {
        this.remediesTexts = remediesTexts;
    }

    @Override
    public RemediesResponse applyRemedy(final Context context, final RemediesRequest remediesRequest, final RemediesResponse remediesResponse) {

        //Se deja el codigo para seteo de un remedy default.
        /*
        final String title = remediesTexts.getTranslation(context.getLocale(), KEY_TITLE);

        final String message = remediesTexts.getTranslation(context.getLocale(), KEY_MESSAGE);

        final ResponseRemedyDefault responseRemedyDefault = new ResponseRemedyDefault(title, message);

        remediesResponse.setResponseWithOutRemedy(responseRemedyDefault);
        */

        DatadogRemediesMetrics.trackRemediesInfo(REMEDIES_COUNTER, context, remediesRequest);

        return remediesResponse;
    }
}
