package com.mercadolibre.controllers;

import com.mercadolibre.api.PreferenceAPI;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentRequest;
import com.mercadolibre.dto.payment.PaymentRequestBody;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.dto.public_key.PublicKeyInfo;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.service.AuthService;
import com.mercadolibre.service.PaymentService;
import com.mercadolibre.utils.HeadersUtils;
import com.mercadolibre.utils.logs.LogBuilder;
import com.mercadolibre.validators.PaymentRequestBodyValidator;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;

import java.util.Optional;

import static com.mercadolibre.constants.HeadersConstants.REQUEST_ID;

public enum PaymentsController {

    INSTANCE;

    private static final Logger LOG = Logger.getLogger(PaymentsController.class);
    private static final String CONTROLLER_NAME = "PaymentsController";


    /**
     * Arma un PaymentRequestBody y llama al paymentService para realizar el pago
     *
     * @param request  request
     * @param response response
     * @return payment
     * @throws ApiException        si falla el api call (status code is not 2xx)
     * @throws ValidationException falla la validacion
     */
    public Payment doPayment(final Request request, final Response response) throws ApiException {
        final PaymentRequest paymentRequest = getPaymentRequest(request);
        final Payment payment = PaymentService.INSTANCE.doPayment(paymentRequest);
        logPayment(paymentRequest, payment);
        return payment;
    }

    /**
     * Mapea el body del request a un paymentRequestBody
     * Valida los campos del paymentRequestBody
     * Obtiene la publicKey y la preferencia
     * Crea el objeto PaymentRequest
     *
     * @param request request
     * @return instancia el paymentRequest
     * @throws ValidationException validation exception
     * @throws ApiException        api exception
     */
    private PaymentRequest getPaymentRequest(final Request request) throws ApiException {
        final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(request.body(), PaymentRequestBody.class);
        final PaymentRequestBodyValidator validator = new PaymentRequestBodyValidator();
        validator.validate(paymentRequestBody);
        final String requestId = request.attribute(REQUEST_ID);
        final PublicKeyInfo publicKey = AuthService.INSTANCE.getPublicKey(requestId, paymentRequestBody.getPublicKey());
        final Optional<Preference> preference = PreferenceAPI.INSTANCE.getPreference(requestId, paymentRequestBody.getPrefId());
        return new PaymentRequest(HeadersUtils.fromSparkHeaders(request), paymentRequestBody, publicKey, preference.orElse(null), requestId);
    }

    private void logPayment(final PaymentRequest paymentRequest, final Payment payment) {
        final LogBuilder logBuilder = new LogBuilder(LogBuilder.LEVEL_INFO, LogBuilder.REQUEST_IN)
                .withSource(CONTROLLER_NAME)
                .withStatus(HttpStatus.SC_OK)
                .withCallerId(String.valueOf(paymentRequest.getCallerId()))
                .withClientId(String.valueOf(paymentRequest.getClientId()))
                .withPaymentMethodId(paymentRequest.getBody().getPaymentMethodId())
                .withUserAgent(paymentRequest.getUserAgent());

        LOG.info(logBuilder.build());
    }
}
