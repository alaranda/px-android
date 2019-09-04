package com.mercadolibre.controllers;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.payment.*;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.service.PaymentService;
import com.mercadolibre.utils.HeadersUtils;
import com.mercadolibre.utils.datadog.DatadogTransactionsMetrics;
import com.mercadolibre.validators.PaymentDataValidator;
import com.mercadolibre.validators.PaymentRequestBodyValidator;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import java.util.concurrent.ExecutionException;

import static com.mercadolibre.constants.Constants.ORDER_TYPE_NAME;
import static com.mercadolibre.constants.HeadersConstants.REQUEST_ID;
import static com.mercadolibre.px.toolkit.utils.logs.LogBuilder.requestInLogBuilder;

public enum PaymentsController {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger();
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

        final Context context = new Context.Builder(request.attribute(REQUEST_ID)).build();
        final PaymentRequest paymentRequest = getLegacyPaymentRequest(request, context);
        final Payment payment = PaymentService.INSTANCE.doPayment(context, paymentRequest);
        DatadogTransactionsMetrics.addLegacyPaymentsTransactionData(payment, Constants.FLOW_NAME_LEGACY_PAYMENTS);
        logPayment(context, paymentRequest, payment);
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
    private PaymentRequest getLegacyPaymentRequest(final Request request, final Context context) throws ApiException, ExecutionException, InterruptedException {

        final Headers headers = HeadersUtils.fromSparkHeaders(request);
        final PaymentRequestBody paymentRequestBody = getPaymentRequestBody(request);
        final String publicKeyId = request.queryParams(Constants.PUBLIC_KEY)  != null ? request.queryParams(Constants.PUBLIC_KEY) : paymentRequestBody.getPublicKey();

        if (StringUtils.isBlank(paymentRequestBody.getPublicKey()) && StringUtil.isBlank(request.queryParams(Constants.PUBLIC_KEY))){
            throw new ValidationException("public key required");
        }
        return PaymentService.INSTANCE.getPaymentRequestLegacy(context, paymentRequestBody, publicKeyId, headers);
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

        final Context context = new Context.Builder(request.attribute(REQUEST_ID)).build();
        final PaymentRequest paymentRequest = getPaymentRequest(request, context);
        final Payment payment = PaymentService.INSTANCE.doPayment(context, paymentRequest);
        final String flow = request.queryParams(Constants.CALLER_ID_PARAM) != null ? Constants.FLOW_NAME_PAYMENTS_BLACKLABEL : Constants.FLOW_NAME_PAYMENTS_WHITELABEL;
        DatadogTransactionsMetrics.addPaymentsTransactionData(payment, flow);
        logPayment(context, paymentRequest, payment);

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
    private PaymentRequest getPaymentRequest(final Request request, final Context context) throws ApiException, ExecutionException, InterruptedException {

        final String publicKeyId = request.queryParams(Constants.PUBLIC_KEY);
        final String callerId = request.queryParams(Constants.CALLER_ID_PARAM);
        final String clientId = request.queryParams(Constants.CLIENT_ID_PARAM);

        final PaymentDataBody paymentDataBody = getListPaymentData(request);

        final Headers headers = HeadersUtils.fromSparkHeaders(request);
        return PaymentService.INSTANCE.getPaymentRequest(context, paymentDataBody, publicKeyId, callerId, clientId, headers);
    }

    private PaymentDataBody getListPaymentData(final Request request) throws ApiException {

        try {
            final PaymentDataBody paymentDataBody = GsonWrapper.fromJson(request.body(), PaymentDataBody.class);

            if (StringUtil.isBlank(request.queryParams(Constants.PUBLIC_KEY))) {
                throw new ValidationException("public key required");
            }

            if ((paymentDataBody.getPaymentData().size() != 1) || (StringUtils.isBlank(paymentDataBody.getPrefId())) ) {
                //TODO Hacer desarrollo cuando se implemente pago de pref.
                throw new ApiException("internal_error", "unsupported payment", HttpStatus.SC_BAD_REQUEST);
            }

            final PaymentDataValidator validator = new PaymentDataValidator();
            paymentDataBody.getPaymentData().forEach(paymentData -> {
                validator.validate(paymentData);
            });

            return paymentDataBody;
        } catch (Exception e) {
            throw new ApiException("Bad Request", "Error parsing body", HttpStatus.SC_BAD_REQUEST);
        }
    }

    private void logPayment(final Context context, final PaymentRequest paymentRequest, final Payment payment) {

        logger.info(requestInLogBuilder(context.getRequestId())
                .withSource(CONTROLLER_NAME)
                .withStatus(HttpStatus.SC_OK)
                .withCallerId(String.valueOf(paymentRequest.getCallerId()))
                .withClientId(String.valueOf(paymentRequest.getCallerId()))
                .withPaymentStatus(payment.getStatus())
                .withPaymentStatusDetail(payment.getStatusDetail())
                .withTag(ORDER_TYPE_NAME, payment.getOperationType())
                .withMessage(payment.toLog(payment))
                .build());
    }
}
