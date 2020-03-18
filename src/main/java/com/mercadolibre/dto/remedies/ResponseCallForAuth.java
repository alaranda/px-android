package com.mercadolibre.dto.remedies;

public class ResponseCallForAuth {

    private final String title;
    private final String message;


    public ResponseCallForAuth(final String title, final String message){
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
