package com.mercadolibre.dto.congrats;

import java.util.Set;

public class Congrats {

    private Points mpuntos;
    private Discounts discounts;
    private Set<CrossSelling> crossSelling;

    public Congrats(final Points points, final Discounts discounts, final Set<CrossSelling> crossSelling) {
        this.mpuntos = points;
        this.discounts = discounts;
        this.crossSelling = crossSelling;
    }

    public boolean hasPoints() {
        return  null != this.mpuntos ? true : false;
    }

    public boolean hasDiscounts() {
        return  null != this.discounts ? true : false;
    }

    public Discounts getDiscounts() {
        return discounts;
    }

    public Set<CrossSelling> getCrossSelling() {
        return crossSelling;
    }
}
