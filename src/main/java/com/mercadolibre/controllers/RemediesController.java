package com.mercadolibre.controllers;

import com.mercadolibre.dto.remedies.RemediesRequest;
import com.mercadolibre.dto.remedies.RemediesResponse;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.service.RemediesService;
import com.mercadolibre.utils.Locale;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import static com.mercadolibre.constants.Constants.PAYMENT_ID;
import static com.mercadolibre.constants.QueryParamsConstants.FLOW_NAME;
import static com.mercadolibre.constants.QueryParamsConstants.PLATFORM;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_SITE_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CLIENT_ID;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.REQUEST_ID;

public class RemediesController {

    private static final Logger LOGGER = LogManager.getLogger();

    private final RemediesService remediesService;

    public RemediesController(){
        this.remediesService = new RemediesService();
    }

    /**
     * Recibe un payment id, y en base a eso se fija el status detail e intenta recueprar el pago faliido.
     *
     * @param request  request
     * @param response response
     * @return void
     */
    public RemediesResponse getRemedy(final Request request, final Response response) throws ApiException {

        final String paymentId = request.params(PAYMENT_ID);

        validateParams(paymentId);

        final Context context = Context.builder().requestId(request.attribute(REQUEST_ID))
                .locale(Locale.getLocale(request)).plattform(request.queryParams(PLATFORM)).flow(request.queryParams(FLOW_NAME)).build();

        final RemediesRequest remediesRequest = getRemedyRequest(request);

        final RemediesResponse remediesResponse = remediesService.getRemedy(context, paymentId, remediesRequest);

        return remediesResponse;
    }

    private RemediesRequest getRemedyRequest(final Request request) throws ApiException {
        try {
            final RemediesRequest remediesRequest = GsonWrapper.fromJson(request.body(), RemediesRequest.class);
            remediesRequest.setSiteId(request.queryParams(CALLER_SITE_ID));
            remediesRequest.setUserAgent(UserAgent.create(request.userAgent()));
            remediesRequest.setUserId(request.queryParams(CALLER_ID));
            return remediesRequest;
        } catch (Exception e) {
            throw new ApiException("Bad Request", "Error parsing body", HttpStatus.SC_BAD_REQUEST);
        }
    }

    private void validateParams(final String paymentId) {

        if (StringUtils.isBlank(paymentId)) {
            final ValidationException validationException = new ValidationException("payment id required");
            LOGGER.error(validationException);
            throw validationException;
        }
    }

}
