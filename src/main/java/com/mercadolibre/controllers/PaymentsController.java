package com.mercadolibre.controllers;

import com.mercadolibre.api.PreferenceAPI;
import com.mercadolibre.constants.Constants;
import com.mercadolibre.dto.ApiError;
import com.mercadolibre.dto.access_token.AccessToken;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentRequest;
import com.mercadolibre.dto.payment.PaymentRequestBody;
import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.dto.public_key.PublicKeyInfo;
import com.mercadolibre.exceptions.ApiException;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.service.AuthService;
import com.mercadolibre.service.PaymentService;
import com.mercadolibre.utils.Either;
import com.mercadolibre.utils.HeadersUtils;
import com.mercadolibre.utils.logs.LogBuilder;
import com.mercadolibre.validators.PaymentRequestBodyValidator;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    public Payment doPayment(final Request request, final Response response) throws ApiException, ExecutionException, InterruptedException {
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
    private PaymentRequest getPaymentRequest(final Request request) throws ApiException, ExecutionException, InterruptedException {

        final String publicKeyId = request.queryParams(Constants.PUBLIC_KEY);
        final PaymentRequestBody paymentRequestBody = getPaymentRequestBody(request, publicKeyId);
        final String requestId = request.attribute(REQUEST_ID);

        final CompletableFuture<Either<PublicKeyInfo, ApiError>> futurePk =
                AuthService.INSTANCE.getAsyncPublicKey(publicKeyId, requestId);
        final CompletableFuture<Either<Preference, ApiError>> futurePref =
                PreferenceAPI.INSTANCE.geAsynctPreference(paymentRequestBody.getPrefId(), requestId);

        CompletableFuture.allOf(futurePk, futurePref);

        if (!futurePk.get().isValuePresent()) {
            final ApiError apiError = futurePk.get().getAlternative();
            throw new ApiException("external_error", "API call to public key failed", apiError.getStatus());
        }
        if (!futurePref.get().isValuePresent()) {
            final ApiError apiError = futurePref.get().getAlternative();
            throw new ApiException("external_error", "API call to preference failed", apiError.getStatus());
        }

        final PublicKeyInfo publicKey = futurePk.get().getValue();
        final Preference preference = futurePref.get().getValue();

        if (StringUtils.isNotBlank(request.queryParams(Constants.ACCESS_TOKEN)) || (StringUtils.isNotBlank(request.queryParams(Constants.CLIENT_ID_PARAM))
                && StringUtils.isNotBlank(request.queryParams(Constants.CALLER_ID_PARAM)))) {
            final AccessToken accessToken = AuthService.INSTANCE.getAccessToken(requestId, request.queryParams(Constants.ACCESS_TOKEN));
            return createBlackLabelRequest(request, paymentRequestBody, preference, publicKey, requestId, accessToken);
        }
        return PaymentRequest.builder(HeadersUtils.fromSparkHeaders(request), paymentRequestBody, preference, requestId, false)
                .withCallerId(publicKey.getOwnerId())
                .withClientId(publicKey.getClientId())
                .withHeaderTestToken(publicKeyId)
                .build();
    }

    private PaymentRequest createBlackLabelRequest(final Request request, final PaymentRequestBody paymentRequestBody,
                                                   final Preference preference, final PublicKeyInfo publicKey,
                                                   final String requestId, final AccessToken accessToken) {

        return PaymentRequest.builder(HeadersUtils.fromSparkHeaders(request), paymentRequestBody, preference, requestId, true)
                .withCallerId(Long.valueOf(accessToken.getUserId()))
                .withClientId(accessToken.getClientId())
                .withCollector(publicKey.getOwnerId())
                .build();
    }

    private PaymentRequestBody getPaymentRequestBody(final Request request, final String publicKey) throws ApiException {
        try {
            final PaymentRequestBody paymentRequestBody = GsonWrapper.fromJson(request.body(), PaymentRequestBody.class);
            if (StringUtil.isBlank(publicKey)) {
                throw new ValidationException("public key required");
            }
            final PaymentRequestBodyValidator validator = new PaymentRequestBodyValidator();
            validator.validate(paymentRequestBody);
            return paymentRequestBody;
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
