package com.mercadolibre.dto.remedies;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseCvv {

    private final String title;
    private final String message;
    private final FieldSetting fieldSetting;

}
