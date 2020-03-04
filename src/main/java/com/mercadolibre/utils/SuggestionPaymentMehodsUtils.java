package com.mercadolibre.utils;

import com.mercadolibre.dto.remedies.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedies.Installment;
import com.mercadolibre.dto.remedies.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedies.RemediesRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public final class SuggestionPaymentMehodsUtils {

    public static AlternativePayerPaymentMethod findPaymentMethodEqualsAmount(final RemediesRequest remediesRequest) {

        final PayerPaymentMethodRejected payerPaymentMethodRejected = remediesRequest.getPayerPaymentMethodRejected();

        final List<AlternativePayerPaymentMethod> alternativePayerPaymentMethodList =  remediesRequest.getAlternativePayerPaymentMethods();

        AlternativePayerPaymentMethod alternativePayerPaymentMethodSelected = null;

        for (AlternativePayerPaymentMethod alternativePayerPaymentMethod : alternativePayerPaymentMethodList) {

            final Installment installment = findEqualsInstallment(alternativePayerPaymentMethod.getInstallments(),
                    payerPaymentMethodRejected.getInstallments(), payerPaymentMethodRejected.getTotalAmount());

            if (null != installment) {
                alternativePayerPaymentMethodSelected =  new AlternativePayerPaymentMethod(alternativePayerPaymentMethod.getPaymentMethodId(),
                        alternativePayerPaymentMethod.getPaymentTypeId(), Arrays.asList(installment), alternativePayerPaymentMethod.isEsc());
                break;
            }
        }

        return alternativePayerPaymentMethodSelected;
    }


    private static Installment findEqualsInstallment(final List<Installment> installments, final int installmentQuantity, final BigDecimal totalAmount) {

        Installment installmentSelected = null;

        for (Installment installment : installments) {

            if ((installment.getInstallments() == installmentQuantity) && (installment.getTotalAmount().equals(totalAmount))) {
                installmentSelected = installment;
            }
        }

        return installmentSelected;
    }

}
