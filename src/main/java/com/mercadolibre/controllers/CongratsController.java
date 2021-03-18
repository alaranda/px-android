package com.mercadolibre.controllers;

import static com.mercadolibre.constants.Constants.LOCATION_FALSE;
import static com.mercadolibre.constants.QueryParamsConstants.*;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_SITE_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CLIENT_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.REQUEST_ID;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.DENSITY;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.LANGUAGE;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.PRODUCT_ID;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.SESSION_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder.REQUEST_IN;
import static com.mercadolibre.utils.HeadersUtils.X_LOCATION_ENABLED;
import static com.mercadolibre.utils.HeadersUtils.userAgentFromHeader;
import static org.apache.http.protocol.HTTP.USER_AGENT;

import com.mercadolibre.dto.congrats.Congrats;
import com.mercadolibre.dto.congrats.CongratsRequest;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder;
import com.mercadolibre.service.CongratsService;
import com.mercadolibre.utils.datadog.DatadogCongratsMetric;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

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
   * @param request request
   * @param response response
   * @return congrats
   * @throws ApiException API Exception
   */
  public Congrats getCongrats(final Request request, final Response response) throws ApiException {

    final String language = request.headers(LANGUAGE);
    if (null == language) {
      throw new ValidationException("language required");
    }

    final Context context =
        Context.builder()
            .requestId(request.attribute(REQUEST_ID))
            .locale(language, request.queryParams(CALLER_SITE_ID))
            .userAgent(userAgentFromHeader(request.headers(USER_AGENT)))
            .build();

    final CongratsRequest congratsRequest = getCongratsRequest(request);

    DatadogCongratsMetric.requestCongratsMetric(congratsRequest);
    LOGGER.info(
        new LogBuilder(context.getRequestId(), REQUEST_IN)
            .withSource(CONTROLLER_NAME)
            .withMethod(request.requestMethod())
            .withUrl(request.url())
            .withUserAgent(request.userAgent())
            .withSessionId(request.headers(SESSION_ID))
            .withAcceptLanguage(context.getLocale().toString())
            .withParams(congratsRequest.toString())
            .build());

    final Congrats congrats = congratsService.getPointsAndDiscounts(context, congratsRequest);

    DatadogCongratsMetric.trackCongratsData(congrats, congratsRequest);
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
    final boolean ifpe = Boolean.parseBoolean(request.queryParams(IFPE));
    final String paymentMethodsIds = request.queryParams(PAYMENT_METHODS_IDS);
    final String preferenceId = request.queryParams(PREF_ID);
    final String merchantOrderId = request.queryParams(MERCHANT_ORDER_ID);
    final String merchantAccountId = request.queryParams(MERCHANT_ACCOUNT_ID);

    final String locationEnabled =
        request.headers(X_LOCATION_ENABLED) != null
            ? request.headers(X_LOCATION_ENABLED)
            : LOCATION_FALSE;

    return new CongratsRequest(
        callerId,
        clientId,
        siteId,
        paymentIds,
        platform,
        userAgent,
        density,
        productId,
        campaignId,
        flowName,
        ifpe,
        paymentMethodsIds,
        preferenceId,
        locationEnabled,
        merchantOrderId,
        merchantAccountId);
  }
}
