package com.mercadolibre.dto;

import com.mercadolibre.constants.Constants;

public class Order {

    private final long id;
    private final String type;

    public Order(final long id, final String type) {
        this.id = id;
        if (null != type){
            this.type = type;
        } else {
            this.type = Constants.MERCHANT_ORDER_TYPE_MP;
        }

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
