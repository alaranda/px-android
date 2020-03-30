package com.mercadolibre.dto.remedy;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseBadFilledDate {

    private final String title;
    private final String message;
    private final FieldSetting fieldSetting;

}
