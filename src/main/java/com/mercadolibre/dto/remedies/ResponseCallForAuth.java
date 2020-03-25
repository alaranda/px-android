package com.mercadolibre.dto.remedies;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseCallForAuth {

    private final String title;
    private final String message;
}
