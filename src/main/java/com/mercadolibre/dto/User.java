package com.mercadolibre.dto;

public final class User {

    private String id;
    private Identification identification;
    private String email;

    User() {};

    public String getId() {
        return id;
    }

    public Identification getIdentification() {
        return identification;
    }

    public String getEmail() {
        return email;
    }
}
