package com.mercadolibre.dto.remedies;

public class ResponseHighRisk {

    private String title;
    private String message;
    private String deepLink;

    public ResponseHighRisk(final String title, final String message, final String deepLink) {
        this.title = title;
        this.message = message;
        this.deepLink = deepLink;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDeepLink() {
        return deepLink;
    }
}
