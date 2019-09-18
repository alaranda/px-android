package com.mercadolibre.dto.congrats;

public class DiscountItem {

    private String icon;
    private String title;
    private String subtitle;
    private String target;
    private String campaignId;

    public DiscountItem(final String icon, final String title, final String subtitle, final  String target, final String campaingId) {
        this.icon = icon;
        this.title = title;
        this.subtitle = subtitle;
        this.target = target;
        this.campaignId = campaingId;
    }

}
