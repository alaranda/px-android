package com.mercadolibre.dto;

public class Order {

    private final long id;
    private final String type;

    public Order(final long id, final String type) {
        this.id = id;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Order{"
                + "id=" + id
                + ", type='" + type + '\''
                + '}';
    }

}
