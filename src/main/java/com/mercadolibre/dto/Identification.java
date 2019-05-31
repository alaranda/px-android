package com.mercadolibre.dto;

public class Identification {

    private String number;
    private String type;

    Identification() {

    }

    public Identification(final String idType, final String idNumber) {
        this.type = idType;
        this.number = idNumber;
    }

    public String getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

}
