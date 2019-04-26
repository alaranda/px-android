package com.mercadolibre.dto;

public class Payer {

    private String id;
    private String accessToken;
    private Identification identification;
    private String type;
    private String email;
    private String firstName;
    private String lastName;


    public Payer(final String accessToken) {
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public Identification getIdentification() {
        return identification;
    }

    public String getType() {
        return type;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
