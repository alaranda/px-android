package com.mercadolibre.dto;

public class Address {

    private String zipCode;
    private String streetName;
    private String streetNumber;

    Address() {
    }

    public String getStreetName() {
        return streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getZipCode() {
        return zipCode;
    }
}
