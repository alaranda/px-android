package com.mercadolibre.controllers;

import com.mercadolibre.dto.fraud.ResetStatus;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.service.CapEscService;
import com.mercadolibre.utils.Locale;
import com.mercadolibre.utils.logs.RequestLogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

import static com.mercadolibre.px.toolkit.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.X_CLIENT_ID;

public class CapEscController {

    private final CapEscService capEscService;

    public CapEscController() {
        this.capEscService = new CapEscService();
    }

    private static final Logger logger = LogManager.getLogger();



    public ResetStatus resetCapEsc(final Request request, final Response response) throws ApiException {

        RequestLogUtils.logRawRequest(request);

        final Context context = Context.builder().requestId(request.attribute(REQUEST_ID))
                .locale(Locale.getLocale(request)).build();

        final String cardId = request.params("cardId");
        if (null == cardId) {
            throw new ValidationException("card_id is required");
        }

        final String clientId = request.headers(X_CLIENT_ID);
        if (null == cardId) {
            throw new ValidationException("x-client-id is required");
        }

        final ResetStatus resetStatus = capEscService.resetCapEsc(context, cardId, clientId);

        return resetStatus;
    }
}
