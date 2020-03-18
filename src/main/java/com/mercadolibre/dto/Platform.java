package com.mercadolibre.dto;

public enum Platform {

    MERCADOPAGO("MP"),
    MERCADOLIBRE("ML");

    private String id;

    Platform(final String id) {this.id = id; }

    public String getId() {return id; }

    public static String from(final String platformId) {
        for (final Platform platform : values()) {
            if (platform.id.equalsIgnoreCase(platformId)) {
                return platform.toString();
            }
        }
        throw new IllegalStateException("platform undefined");
    }
}
