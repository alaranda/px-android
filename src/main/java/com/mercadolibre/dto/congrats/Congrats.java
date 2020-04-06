package com.mercadolibre.dto.congrats;

import com.mercadolibre.px.dto.lib.text.Text;

import java.util.Set;

public class Congrats {

    private Points mpuntos;
    private Discounts discounts;
    private Set<CrossSelling> crossSelling;
    private Action viewReceipt;
    private Text topTextBox;

    public Congrats(final Points points, final Discounts discounts, final Set<CrossSelling> crossSelling,
                    final Action viewReceipt, final Text topTextBox) {
        this.mpuntos = points;
        this.discounts = discounts;
        this.crossSelling = crossSelling;
        this.viewReceipt = viewReceipt;
        this.topTextBox = topTextBox;
    }

    public Congrats(){
        this.mpuntos =null;
        this.discounts = null;
        this.crossSelling = null;
        this.viewReceipt = null;
        this.topTextBox = null;
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

    public Points getMpuntos() {return mpuntos; }

    public Action getViewReceipt() {
        return viewReceipt;
    }

    public Text getTopTextBox() {
        return topTextBox;
    }

    public String toString() {
        return String.format("Congrats{Points=[%s], Discounts=[%s]}",
                mpuntos.toString(), discounts.toString());
    }
}
