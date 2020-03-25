package com.mercadolibre.dto.remedies;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FieldSetting {

    private String name;
    private int length;
    private String type;
    private String title;
    private String hintMessage;
    private String validationMessage;
    private String mask;

}
