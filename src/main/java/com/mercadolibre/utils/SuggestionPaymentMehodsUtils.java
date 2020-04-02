package com.mercadolibre.utils;

import com.mercadolibre.dto.remedy.AlternativePayerPaymentMethod;
import com.mercadolibre.dto.remedy.Installment;
import com.mercadolibre.dto.remedy.PayerPaymentMethodRejected;
import com.mercadolibre.dto.remedy.RemediesRequest;

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
                alternativePayerPaymentMethodSelected = AlternativePayerPaymentMethod.builder()
                        .paymentMethodId(alternativePayerPaymentMethod.getPaymentMethodId())
                        .paymentTypeId(alternativePayerPaymentMethod.getPaymentTypeId())
                        .installments(Arrays.asList(installment))
                        .esc(alternativePayerPaymentMethod.isEsc())
                        .build();

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
