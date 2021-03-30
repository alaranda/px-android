package com.mercadolibre.controllers;

import static com.mercadolibre.px.constants.HeadersConstants.*;

import com.mercadolibre.dto.fraud.ResetStatus;
import com.mercadolibre.px.constants.CommonParametersNames;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.exceptions.ValidationException;
import com.mercadolibre.px.monitoring.lib.log.LogBuilder;
import com.mercadolibre.service.CapEscService;
import com.mercadolibre.utils.assemblers.ContextAssembler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

public class CapEscController {

  private final CapEscService capEscService;

  private static final String CONTROLLER_NAME = "CapEscController";

  public CapEscController() {
    this.capEscService = new CapEscService();
  }

  private static final Logger LOGGER = LogManager.getLogger();

  public ResetStatus resetCapEsc(final Request request, final Response response)
      throws ApiException {

    final Context context = ContextAssembler.toContext(request);

    LOGGER.info(
        new LogBuilder(context.getRequestId(), LogBuilder.REQUEST_IN)
            .withSource(CONTROLLER_NAME)
            .withMethod(request.requestMethod())
            .withUrl(request.url())
            .withUserAgent(request.userAgent())
            .withSessionId(request.headers(SESSION_ID))
            .withAcceptLanguage(context.getLocale().toString())
            .withParams(request.queryString())
            .build());

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
