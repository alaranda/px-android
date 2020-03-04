package com.mercadolibre.dto.remedies;

import java.util.List;

public class AlternativePayerPaymentMethod {

    private final String paymentMethodId;
    private final String paymentTypeId;
    private final List<Installment> installments;
    private int selected_payer_cost_index;
    private final boolean esc;

    public AlternativePayerPaymentMethod(final String paymentMethodId, final String paymentTypeId,
                                         final List<Installment> installments, final boolean esc) {
        this.paymentMethodId = paymentMethodId;
        this.paymentTypeId = paymentTypeId;
        this.installments = installments;
        this.esc = esc;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public List<Installment> getInstallments() {
        return installments;
    }

    public boolean isEsc() {
        return esc;
    }
}
