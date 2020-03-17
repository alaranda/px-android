package com.mercadolibre.dto;

import com.mercadolibre.px.dto.lib.user.Identification;

public final class User {

    private Long id;
    private Identification identification;
    private String email;

    User() {};

    public Long getId() {
        return id;
    }

    public Identification getIdentification() {
        return identification;
    }

    public String getEmail() {
        return email;
    }
}
