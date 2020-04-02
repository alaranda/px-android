package com.mercadolibre.service;

import com.mercadolibre.api.PaymentAPI;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.dto.ApiError;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.px.toolkit.utils.Either;
import com.mercadolibre.service.remedy.RemedyInterface;
import com.mercadolibre.service.remedy.RemedyTypes;

import java.util.List;

public class RemediesService {

    private final RemedyTypes remedyTypes;

    public RemediesService() {
        this.remedyTypes = new RemedyTypes();
    }

    /**
     * A partir del "status_detail" del payment, decide el remedy a aplicar (si es que se puede).
     *
     * @param context  context
     * @param paymentId  payment id
     * @param remediesRequest remediesRequest
     * @throws ApiException        api exception
     * @return RemediesResponse
     */
    public RemediesResponse getRemedy(final Context context, final String paymentId, final RemediesRequest remediesRequest) throws ApiException {

        final Either<Payment, ApiError> paymentEither = PaymentAPI.INSTANCE.getPayment(context, paymentId);

        if (! paymentEither.isValuePresent()) {
            throw new ApiException(paymentEither.getAlternative());
        }

        final Payment payment = paymentEither.getValue();
        remediesRequest.setRiskExcecutionId(payment.getRiskExecutionId());
        remediesRequest.setStatusDetail(payment.getStatusDetail());

        final List<RemedyInterface> remediesInterface = remedyTypes.getRemedyByType(payment.getStatusDetail());
        //TODO status detail hardcodeado para pruebas.
        //final List<RemedyInterface> remediesInterface = remedyTypes.getRemedyByType(CC_REJECTED_CALL_FOR_AUTHORIZE.getId());

        final RemediesResponse remediesResponse = new RemediesResponse();

        remediesInterface.forEach( remedyInterface -> {

            remedyInterface.applyRemedy(context, remediesRequest, remediesResponse);

        });

        return remediesResponse;
    }
}
