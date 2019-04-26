package com.mercadolibre.dto;

public class Address {

    private String zipCode;
    private String streetName;
    private Integer streetNumber;

    Address() {
    }

    public String getStreetName() {
        return streetName;
    }

    public Integer getStreetNumber() {
        return streetNumber;
    }

    public String getZipCode() {
        return zipCode;
    }
}
