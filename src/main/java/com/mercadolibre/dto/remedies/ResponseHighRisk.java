package com.mercadolibre.dto.remedies;

public class ResponseHighRisk {

    private String title;
    private String message;

    public ResponseHighRisk(final String title, final String message) {
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
