package com.mercadolibre.dto.preference;


import com.mercadolibre.dto.Item;

public class PreferenceItem extends Item {

    private String currencyId;

    PreferenceItem() {
        super();
    }

    public String getCurrencyId() {
        return currencyId;
    }
}
