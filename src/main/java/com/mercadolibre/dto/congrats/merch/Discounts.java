package com.mercadolibre.dto.congrats.merch;

import java.util.List;

public class Discounts {

    private String fallbackLink;
    private String link;
    private List<Item> items;

    public String getFallbackLink() {
        return fallbackLink;
    }

    public String getLink() {
        return link;
    }

    public List<Item> getItems() {
        return items;
    }
}
