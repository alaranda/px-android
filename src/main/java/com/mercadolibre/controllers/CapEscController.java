package com.mercadolibre.controllers;

import com.mercadolibre.dto.fraud.ResetStatus;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.constants.CommonParametersNames;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder;
import com.mercadolibre.service.CapEscService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

import static com.mercadolibre.px.toolkit.constants.HeadersConstants.LANGUAGE;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.SESSION_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder.REQUEST_IN;

public class CapEscController {

    private final CapEscService capEscService;

    private static final String CONTROLLER_NAME = "CapEscController";

    public CapEscController() {
        this.capEscService = new CapEscService();
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public ResetStatus resetCapEsc(final Request request, final Response response) throws ApiException {

        final Context context = Context.builder().requestId(request.attribute(REQUEST_ID))
                .locale(request.headers(LANGUAGE)).build();

        LOGGER.info(
                new LogBuilder(context.getRequestId(), REQUEST_IN)
                        .withSource(CONTROLLER_NAME)
                        .withMethod(request.requestMethod())
                        .withUrl(request.url())
                        .withUserAgent(request.userAgent())
                        .withSessionId(request.headers(SESSION_ID))
                        .withParams(request.queryParams().toString())
        );

        final String cardId = request.params("cardId");
        if (null == cardId) {
            throw new ValidationException("card_id is required");
        }

        final String clientId = request.queryParams(CommonParametersNames.CLIENT_ID);
        if (null == clientId) {
            throw new ValidationException("client id is required");
        }

        final ResetStatus resetStatus = capEscService.resetCapEsc(context, cardId, clientId);

        return resetStatus;
    }
}
