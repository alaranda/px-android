package com.mercadolibre.dto.payment;

public class BasicUser {

    private final long id;

    /**
     * Basic user constructor
     *
     * @param id the user id
     */
    public BasicUser(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
