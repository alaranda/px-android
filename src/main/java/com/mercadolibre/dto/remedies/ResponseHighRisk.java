package com.mercadolibre.dto.remedies;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseHighRisk {

    private String title;
    private String message;
    private String deepLink;
}
