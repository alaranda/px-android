package com.mercadolibre.controllers;

import com.mercadolibre.dto.congrats.Congrats;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder;
import com.mercadolibre.service.CongratsService;
import com.mercadolibre.utils.datadog.DatadogCongratsMetric;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

import static com.mercadolibre.constants.QueryParamsConstants.CAMPAIGN_ID;
import static com.mercadolibre.constants.QueryParamsConstants.FLOW_NAME;
import static com.mercadolibre.constants.QueryParamsConstants.PAYMENT_IDS;
import static com.mercadolibre.constants.QueryParamsConstants.PLATFORM;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_SITE_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CLIENT_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.REQUEST_ID;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.DENSITY;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.LANGUAGE;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.PRODUCT_ID;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.SESSION_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder.REQUEST_IN;
import static com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder.requestInLogBuilder;

public class CongratsController {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String CONTROLLER_NAME = "CongratsController";

    private final CongratsService congratsService;

    public CongratsController() {
        this.congratsService = new CongratsService();
    }

    /**
     * Recibe un accestoken y pamentsId para mostrarle al usaurio puntos y descuentos.
     *
     * @param request  request
     * @param response response
     * @return congrats
     */
    public Congrats getCongrats(final Request request, final Response response) throws ApiException {

        final String language = request.headers(LANGUAGE);
        if (null == language) { throw new ValidationException("language required"); }

        final Context context = Context.builder().requestId(request.attribute(REQUEST_ID))
                .locale(language, request.queryParams(CALLER_SITE_ID)).build();

        LOGGER.info(
                new LogBuilder(context.getRequestId(), REQUEST_IN)
                        .withSource(CONTROLLER_NAME)
                        .withMethod(request.requestMethod())
                        .withUrl(request.url())
                        .withUserAgent(request.userAgent())
                        .withSessionId(request.headers(SESSION_ID))
                        .withParams(request.queryParams().toString())
        );
        final CongratsRequest congratsRequest =  getCongratsRequest(request);

        final Congrats congrats = congratsService.getPointsAndDiscounts(context, congratsRequest);

        DatadogCongratsMetric.trackCongratsData(congrats, congratsRequest);
        logCongrats(context, congrats);
        return congrats;
    }

    /**
     * Mapea el los queryParams a un congratsRequest
     *
     * @param request request
     * @return instancia del congratsRequest
     */
    private CongratsRequest getCongratsRequest(final Request request) throws ApiException {

        final String callerId = request.queryParams(CALLER_ID);
        if (null == callerId) throw new ValidationException("invalid user");

        final String platform = request.queryParams(PLATFORM);
        if (null == platform) throw new ValidationException("platform required");

        final String density = request.headers(DENSITY);
        if (null == density) throw new ValidationException("density required");

        final String productId = request.headers(PRODUCT_ID);
        if (null == productId) throw new ValidationException("productId required");

        final String paymentIds = request.queryParams(PAYMENT_IDS);
        final String clientId = request.queryParams(CLIENT_ID);
        final String siteId = request.queryParams(CALLER_SITE_ID);
        final UserAgent userAgent = UserAgent.create(request.userAgent());
        final String campaignId = request.queryParams(CAMPAIGN_ID);
        final String flowName = request.queryParams(FLOW_NAME);

        return new CongratsRequest(callerId, clientId, siteId, paymentIds, platform, userAgent, density,
                productId, campaignId, flowName);
    }

    private void logCongrats(final Context context, final Congrats congrats) {
        LOGGER.info(requestInLogBuilder(context.getRequestId())
                .withSource(CongratsController.class.getSimpleName())
                .withStatus(HttpStatus.SC_OK)
                .withMessage(congrats.toString())
                .build());
    }

}
