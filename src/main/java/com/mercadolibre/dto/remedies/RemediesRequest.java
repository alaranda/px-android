package com.mercadolibre.dto.remedies;

import com.mercadolibre.px.toolkit.dto.user_agent.UserAgent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class RemediesRequest {

    private PayerPaymentMethodRejected payerPaymentMethodRejected;
    private List<AlternativePayerPaymentMethod> alternativePayerPaymentMethods;

    @Setter
    private long riskExcecutionId;
    @Setter
    private String statusDetail;
    @Setter
    private UserAgent userAgent;
    @Setter
    private String siteId;
    @Setter
    private String userId;
}
