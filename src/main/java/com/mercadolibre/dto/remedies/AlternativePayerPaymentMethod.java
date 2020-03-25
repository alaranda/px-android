package com.mercadolibre.dto.remedies;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AlternativePayerPaymentMethod {

    private final String paymentMethodId;
    private final String paymentTypeId;
    private final List<Installment> installments;
    private int selected_payer_cost_index;
    private final boolean esc;
}
