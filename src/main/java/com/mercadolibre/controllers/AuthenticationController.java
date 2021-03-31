package com.mercadolibre.controllers;

import static com.mercadolibre.constants.QueryParamsConstants.CARD_TOKEN;
import static com.mercadolibre.px.constants.ErrorCodes.BAD_REQUEST;
import static com.mercadolibre.px.constants.HeadersConstants.*;
import static com.mercadolibre.security.authentication.protocol.PrivateAuthenticationParameters.CALLER_ID;

import com.mercadolibre.dto.cha.CardHolderAuthenticationRequest;
import com.mercadolibre.dto.cha.CardHolderAuthenticationResponse;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.exceptions.ApiException;
import com.mercadolibre.px.exceptions.ValidationException;
import com.mercadolibre.px.monitoring.lib.log.LogBuilder;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.service.CHAService;
import com.mercadolibre.utils.CardHolderAuthenticationUtils;
import com.mercadolibre.utils.assemblers.ContextAssembler;
import java.text.ParseException;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

public class AuthenticationController {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String CONTROLLER_NAME = "AuthenticationController";

  private final CHAService CHAService;

  public AuthenticationController() {
    this.CHAService = new CHAService();
  }

  public CardHolderAuthenticationResponse authenticateCardHolder(
      final Request request, final Response response) throws ApiException {

    final Context context = ContextAssembler.toContext(request);

    final String callerId = request.queryParams(CALLER_ID.getParameterName());
    if (callerId == null) {
      throw new ValidationException("Access token is required");
    }

    final String cardToken = request.queryParams(CARD_TOKEN);
    if (cardToken == null) {
      throw new ValidationException("Card token is required");
    }

    final CardHolderAuthenticationRequest chaRequest = getCardHolderAuthenticationRequest(request);

    if (chaRequest == null) {
      throw new ValidationException("Empty body");
    }

    try {
      CardHolderAuthenticationUtils.validatePurchaseAmount(
          chaRequest.getCurrency().getDecimalSeparator(),
          chaRequest.getCurrency().getThousandsSeparator(),
          chaRequest.getPurchaseAmount());
    } catch (ParseException pa) {
      throw new ApiException(
          BAD_REQUEST, "Error parsing purchase amount", HttpStatus.SC_BAD_REQUEST);
    }

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

    return CHAService.authenticate(context, chaRequest, callerId, cardToken);
  }

  private CardHolderAuthenticationRequest getCardHolderAuthenticationRequest(final Request request)
      throws ApiException {
    try {
      return GsonWrapper.fromJson(request.body(), CardHolderAuthenticationRequest.class);
    } catch (final Exception e) {
      throw new ApiException(BAD_REQUEST, "Error parsing body", HttpStatus.SC_BAD_REQUEST);
    }
  }
}
