package com.mercadolibre.dto.congrats.merch;

import java.util.List;

public class Discounts {

    private String link;
    private int loyaltyDiscounts;
    private Paging paging;
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

    public Paging getPaging() {
        return paging;
    }
}
