package com.mercadolibre.controllers;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.congrats.Congrats;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.dto.user_agent.UserAgent;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.service.CongratsService;
import com.mercadolibre.utils.datadog.DatadogCongratsMetric;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

import java.util.concurrent.ExecutionException;

import static com.mercadolibre.constants.Constants.CLIENT_ID_PARAM;
import static com.mercadolibre.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.constants.QueryParamsConstants.*;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_SITE_ID;
import static com.mercadolibre.px.toolkit.utils.logs.LogBuilder.requestInLogBuilder;

public enum CongratsController {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger();

    /**
     * Recibe un accestoken y pamentsId para mostrarle al usaurio puntos y descuentos.
     *
     * @param request  request
     * @param response response
     * @return congrats
     * @throws ApiException        si falla el api call (status code is not 2xx)
     */
    public Congrats getCongrats(final Request request, final Response response) {

        final Context context = new Context.Builder(request.attribute(REQUEST_ID)).build();
        final CongratsRequest congratsRequest =  getCongratsRequest(request);

        final Congrats congrats = CongratsService.INSTANCE.getPointsAndDiscounts(context, congratsRequest);

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
    private CongratsRequest getCongratsRequest(final Request request) {

        final String callerId = request.queryParams(Constants.CALLER_ID_PARAM);
        final String clientId = request.queryParams(CLIENT_ID_PARAM);
        final String siteId = request.queryParams(CALLER_SITE_ID);
        final String paymentIds = request.queryParams(PAYMENT_IDS);
        final String platform = request.queryParams(PLATFORM);
        final String platformVersion = UserAgent.create(request.userAgent()).getVersion().getVersionName();

        return new CongratsRequest(callerId, clientId, siteId, paymentIds, platform, platformVersion);
    }

    private void logCongrats(final Context context, final Congrats congrats) {
        logger.info(requestInLogBuilder(context.getRequestId())
                .withSource(CongratsController.class.getSimpleName())
                .withStatus(HttpStatus.SC_OK)
                .withMessage(congrats.toString())
                .build());
    }
}
