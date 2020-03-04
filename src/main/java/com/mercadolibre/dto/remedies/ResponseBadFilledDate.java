package com.mercadolibre.dto.remedies;

public class ResponseBadFilledDate {

    private final String title;
    private final String message;
    private final FieldSetting fieldSetting;


    public ResponseBadFilledDate(final String title, final String message, final FieldSetting fieldSetting){
        this.title = title;
        this.message = message;
        this.fieldSetting = fieldSetting;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public FieldSetting getFieldSetting() {
        return fieldSetting;
    }
}
