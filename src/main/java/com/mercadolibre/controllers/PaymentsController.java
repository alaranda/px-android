package com.mercadolibre.controllers;

import com.google.gson.reflect.TypeToken;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentRequest;
import com.mercadolibre.dto.payment.PaymentRequestBody;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.service.PaymentService;
import com.mercadolibre.utils.HeadersUtils;
import com.mercadolibre.utils.datadog.DatadogTransactionsMetrics;
import com.mercadolibre.utils.logs.LogBuilder;
import com.mercadolibre.validators.PaymentRequestBodyValidator;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.mercadolibre.constants.HeadersConstants.REQUEST_ID;

public enum PaymentsController {

    INSTANCE;

    private static final Logger LOG = Logger.getLogger(PaymentsController.class);
    private static final String CONTROLLER_NAME = "PaymentsController";

    /**
     * Endpoint migrado de grails(whitelabel), arma un PaymentRequestBody y llama al paymentService para realizar el pago
     *
     * @param request  request
     * @param response response
     * @return payment
     * @throws ApiException        si falla el api call (status code is not 2xx)
     * @throws ValidationException falla la validacion
     */
    public Payment doLegacyPayment(final Request request, final Response response) throws ApiException, ExecutionException, InterruptedException {
        final PaymentRequest paymentRequest = getLegacyPaymentRequest(request);
        final Payment payment = PaymentService.INSTANCE.doPayment(paymentRequest);
        DatadogTransactionsMetrics.addTransactionData(payment, Constants.FLOW_NAME_LEGACY_PAYMENTS);
        logPayment(paymentRequest, payment);
        return payment;
    }

    /**
     * Endopoint migrado de checkout-off (Grails)
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
    private PaymentRequest getLegacyPaymentRequest(final Request request) throws ApiException, ExecutionException, InterruptedException {

        final String requestId = request.attribute(REQUEST_ID);
        final Headers headers = HeadersUtils.fromSparkHeaders(request);
        final PaymentRequestBody paymentRequestBody = getPaymentRequestBody(request);
        final String publicKeyId = request.queryParams(Constants.PUBLIC_KEY)  != null ? request.queryParams(Constants.PUBLIC_KEY) : paymentRequestBody.getPublicKey();

        if (StringUtils.isBlank(paymentRequestBody.getPublicKey()) && StringUtil.isBlank(request.queryParams(Constants.PUBLIC_KEY))){
            throw new ValidationException("public key required");
        }
        return PaymentService.INSTANCE.getPaymentRequest(paymentRequestBody, publicKeyId, null, requestId, headers);
    }

    private PaymentRequestBody getPaymentRequestBody(final Request request) throws ApiException {
        try {
            final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(request.body(), PaymentRequestBody.class);

            final PaymentRequestBodyValidator validator = new PaymentRequestBodyValidator();
            validator.validate(paymentRequestBody);

            return paymentRequestBody;
        } catch (Exception e) {
            throw new ApiException("Bad Request", "Error parsing body", HttpStatus.SC_BAD_REQUEST);
        }
    }

    /**
     * Nuevo endpoint de pagos que soporta blacklabel, arma un PaymentRequestBody y llama al paymentService para realizar el pago
     *
     * @param request  request
     * @param response response
     * @return payment
     * @throws ApiException        si falla el api call (status code is not 2xx)
     * @throws ValidationException falla la validacion
     */
    public Payment doPayment(final Request request, final Response response) throws ApiException, ExecutionException, InterruptedException {
        final PaymentRequest paymentRequest = getPaymentRequest(request);
        final Payment payment = PaymentService.INSTANCE.doPayment(paymentRequest);

        DatadogTransactionsMetrics.addTransactionData(payment, Constants.FLOW_NAME_PAYMENTS);
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
    private PaymentRequest getPaymentRequest(final Request request) throws ApiException, ExecutionException, InterruptedException {

        final String requestId = request.attribute(REQUEST_ID);
        final String publicKeyId = request.queryParams(Constants.PUBLIC_KEY);
        final String accesToken = request.queryParams(Constants.ACCESS_TOKEN);

        final List<PaymentRequestBody> paymentRequestBodyList = getListPaymentRequestBody(request);
        final PaymentRequestBody paymentRequestBody = paymentRequestBodyList.get(0);

        final Headers headers = HeadersUtils.fromSparkHeaders(request);
        return PaymentService.INSTANCE.getPaymentRequest(paymentRequestBody, publicKeyId, accesToken, requestId, headers);
    }

    private List<PaymentRequestBody> getListPaymentRequestBody(final Request request) throws ApiException {

        try {
            final Type bodyListType = new TypeToken<ArrayList<PaymentRequestBody>>(){}.getType();
            final List<PaymentRequestBody> paymentRequestBodyList = GsonWrapper.fromJson(request.body(), bodyListType);

            if (StringUtil.isBlank(request.queryParams(Constants.PUBLIC_KEY))) {
                throw new ValidationException("public key required");
            }

            if (paymentRequestBodyList.size() != 1){
                //TODO Hacer desarrollo cuando se implemente pago de pref.
                throw new ApiException("internal_error", "unsupported payment", HttpStatus.SC_BAD_REQUEST);
            }

            final PaymentRequestBodyValidator validator = new PaymentRequestBodyValidator();
            paymentRequestBodyList.forEach(paymentRequestBody -> {
                validator.validate(paymentRequestBody);
            });

            return paymentRequestBodyList;
        } catch (Exception e) {
            throw new ApiException("Bad Request", "Error parsing body", HttpStatus.SC_BAD_REQUEST);
        }
    }

    private void logPayment(final PaymentRequest paymentRequest, final Payment payment) {
        final LogBuilder logBuilder = new LogBuilder(LogBuilder.LEVEL_INFO, LogBuilder.REQUEST_IN)
                .withSource(CONTROLLER_NAME)
                .withStatus(HttpStatus.SC_OK)
                .withCallerId(String.valueOf(paymentRequest.getCallerId()))
                .withClientId(String.valueOf(paymentRequest.getClientId()))
                .withPaymentMethodId(paymentRequest.getBody().getPaymentMethodId())
                .withResponse(payment.toString());

        LOG.info(logBuilder.build());
    }
}
