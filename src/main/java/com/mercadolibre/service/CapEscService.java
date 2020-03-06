package com.mercadolibre.service;

import com.mercadolibre.api.FraudApi;
import com.mercadolibre.dto.fraud.ResetStatus;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CapEscService {

    private final FraudApi fraudApi;

    public CapEscService() {
        this.fraudApi = new FraudApi();
    }

    public ResetStatus resetCapEsc(final Context context, final String cardId, final String clientId) throws ApiException {

        return fraudApi.resetCapEsc(context, cardId, clientId);
    }
}
