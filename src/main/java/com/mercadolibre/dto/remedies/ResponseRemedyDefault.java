package com.mercadolibre.dto.remedies;

public class ResponseRemedyDefault {

    private final String title;
    private final String message;


    public ResponseRemedyDefault(final String title, final String message){
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
