package com.mercadolibre.dto;

import static com.mercadolibre.constants.Constants.MERCHANT_ORDER_TYPE_ML;
import static com.mercadolibre.constants.Constants.MERCHANT_ORDER_TYPE_MP;

public class Order {

    private final long id;
    private final String type;

    private Order(final long id, final String type) {
        this.id = id;
        this.type = type;
    }

    public static Order createOrderMP(final long id) {
        return new Order(id, MERCHANT_ORDER_TYPE_MP);
    }

    public static Order createOrderML(final long id) {
        return new Order(id, MERCHANT_ORDER_TYPE_ML);
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Order{"
                + "id=" + id
                + ", type='" + type + '\''
                + '}';
    }

}
