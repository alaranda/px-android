package com.mercadolibre.controllers;

import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentData;
import com.mercadolibre.dto.payment.PaymentDataBody;
import com.mercadolibre.dto.payment.PaymentRequest;
import com.mercadolibre.dto.payment.PaymentRequestBody;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.gson.GsonWrapper;
import com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder;
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

import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CALLER_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.CLIENT_ID;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.PUBLIC_KEY;
import static com.mercadolibre.px.toolkit.constants.CommonParametersNames.REQUEST_ID;
import static com.mercadolibre.px.toolkit.constants.ErrorCodes.INTERNAL_ERROR;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.LANGUAGE;
import static com.mercadolibre.px.toolkit.constants.HeadersConstants.SESSION_ID;
import static com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder.REQUEST_IN;
import static com.mercadolibre.px.toolkit.utils.monitoring.log.LogBuilder.requestInLogBuilder;

public enum PaymentsController {
    INSTANCE
     ;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String CONTROLLER_NAME = "PaymentsController";

    /**
     * Endpoint migrado de grails(whitelabel), arma un PaymentRequestBody y llama al paymentService para realizar el pago
     *
     * @param request  request
     * @param response response
     * @return payment
     * @throws ExecutionException exexution exception
     * @throws ApiException        api exception
     * @throws InterruptedException  interrupted exception
     */
    public Payment doLegacyPayment(final Request request, final Response response) throws ApiException, ExecutionException, InterruptedException {

        final Context context = Context.builder()
                .requestId(request.attribute(REQUEST_ID))
                .locale(request.headers(LANGUAGE))
                .build();
        LOGGER.info(
                new LogBuilder(context.getRequestId(), REQUEST_IN)
                        .withSource(CONTROLLER_NAME)
                        .withMethod(request.requestMethod())
                        .withUrl(request.url())
                        .withUserAgent(request.userAgent())
                        .withSessionId(request.headers(SESSION_ID))
                        .withAcceptLanguage(context.getLocale().toString())
                        .withParams(request.queryString())
                        .build()
        );

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
     * @throws ExecutionException execution exception
     * @throws ApiException        api exception
     * @throws InterruptedException  interrupted exception
     */
    private PaymentRequest getLegacyPaymentRequest(final Request request, final Context context) throws ApiException, ExecutionException, InterruptedException {

        final Headers headers = HeadersUtils.fromSparkHeaders(request);
        final PaymentRequestBody paymentRequestBody = getPaymentRequestBody(request);
        final String publicKeyId = request.queryParams(PUBLIC_KEY)  != null ? request.queryParams(PUBLIC_KEY) : paymentRequestBody.getPublicKey();

        if (StringUtils.isBlank(paymentRequestBody.getPublicKey()) && StringUtil.isBlank(request.queryParams(PUBLIC_KEY))){
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
     * @throws ExecutionException execution exception
     * @throws ApiException        api exception
     * @throws InterruptedException  interrupted exception
     */
    public Payment doPayment(final Request request, final Response response) throws ApiException, ExecutionException, InterruptedException {

        final Context context = Context.builder()
                .requestId(request.attribute(REQUEST_ID))
                .locale(request.headers(LANGUAGE))
                .build();
        final PaymentRequest paymentRequest = getPaymentRequest(request, context);
        LOGGER.info(
                new LogBuilder(context.getRequestId(), REQUEST_IN)
                        .withSource(CONTROLLER_NAME)
                        .withMethod(request.requestMethod())
                        .withUrl(request.url())
                        .withUserAgent(request.userAgent())
                        .withSessionId(request.headers(SESSION_ID))
                        .withParams(request.queryString())
                        .withPreferenceId(paymentRequest.getPreference().getId())
                        .withAcceptLanguage(context.getLocale().toString())
                        .build()
        );

        final Payment payment = PaymentService.INSTANCE.doPayment(context, paymentRequest);
        final String flow = request.queryParams(CALLER_ID) != null ? Constants.FLOW_NAME_PAYMENTS_BLACKLABEL : Constants.FLOW_NAME_PAYMENTS_WHITELABEL;
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
     * @throws ExecutionException execution exception
     * @throws ApiException        api exception
     * @throws InterruptedException  interrupted exception
     */
    private PaymentRequest getPaymentRequest(final Request request, final Context context) throws ApiException, ExecutionException, InterruptedException {

        final String publicKeyId = request.queryParams(PUBLIC_KEY);
        final String callerId = request.queryParams(CALLER_ID);
        final String clientId = request.queryParams(CLIENT_ID);

        final PaymentDataBody paymentDataBody = getListPaymentData(request);

        final Headers headers = HeadersUtils.fromSparkHeaders(request);
        return PaymentService.INSTANCE.getPaymentRequest(context, paymentDataBody, publicKeyId, callerId, clientId, headers);
    }

    private PaymentDataBody getListPaymentData(final Request request) throws ApiException {

        try {
            final PaymentDataBody paymentDataBody = GsonWrapper.fromJson(request.body(), PaymentDataBody.class);

            if (StringUtil.isBlank(request.queryParams(PUBLIC_KEY))) {
                throw new ValidationException("public key required");
            }

            if ((paymentDataBody.getPaymentData().size() != 1) || (StringUtils.isBlank(paymentDataBody.getPrefId()))) {
                //TODO Hacer desarrollo cuando se implemente pago de pref.
                throw new ApiException(INTERNAL_ERROR, "unsupported payment", HttpStatus.SC_BAD_REQUEST);
            }

            final PaymentDataValidator validator = new PaymentDataValidator();

            for(PaymentData paymentData : paymentDataBody.getPaymentData()) {
                validator.validate(paymentData);
            }

            return paymentDataBody;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Bad Request", "Error parsing body", HttpStatus.SC_BAD_REQUEST);
        }
    }

    private void logPayment(final Context context, final PaymentRequest paymentRequest, final Payment payment) {
        LOGGER.info(requestInLogBuilder(context.getRequestId())
                .withSource(CONTROLLER_NAME)
                .withStatus(HttpStatus.SC_OK)
                .withCallerId(String.valueOf(paymentRequest.getCallerId()))
                .withClientId(String.valueOf(paymentRequest.getCallerId()))
                .withMessage(payment.toLog(payment))
                .build());
    }
}
