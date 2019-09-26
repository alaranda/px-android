package com.mercadolibre.dto.congrats.merch;

import java.util.List;

public class Discounts {

    private String link;
    private int loyaltyDiscounts;
    private int total_discounts;
    private List<Item> items;


    public String getLink() {
        return link;
    }

    public List<Item> getItems() {
        return items;
    }

    public int getLoyaltyDiscounts() {
        return loyaltyDiscounts;
    }

    public int getTotal_discounts() { return total_discounts; }
}
