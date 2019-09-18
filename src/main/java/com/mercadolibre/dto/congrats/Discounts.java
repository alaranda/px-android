package com.mercadolibre.dto.congrats;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Discounts {

    private String title;
    private String subtitle;
    private Action action;
    private Action actionDownload;
    private Set<DiscountItem> items;

    public Discounts(final Builder builder) {
        this.title = builder.title;
        this.subtitle = builder.subtitle;
        this.action = builder.action;
        this.actionDownload = builder.actionDownload;
        this.items = builder.items;
    }

    public static class Builder {

        private String title;
        private String subtitle;
        private Action action;
        private Action actionDownload;
        private Set<DiscountItem> items;

        public Builder(final com.mercadolibre.dto.congrats.merch.Discounts discounts) {

            if (null == discounts){
                return;
            }
            this.title = "FALTA QUE NOS MANDEN EL TITLE";
            this.subtitle = "FALTA QUE NOS MANDEN EL SUBTITLE";
            this.action = new Action(null, discounts.getLink());
            this.actionDownload = new Action(null, discounts.getFallbackLink());

            Set<DiscountItem> items = new HashSet<>();
            if (null == discounts.getItems()){
                return;
            }
            discounts.getItems().stream().map(item ->
                    items.add(new DiscountItem( item.getImage(), item.getTitle(), item.getSubtitle(), item.getLink(), String.valueOf(item.getCampaignId()))))
                    .collect(Collectors.toSet());

            this.items = items;
        }

        public Discounts build() { return new Discounts(this ); }
    }

    public int discountsSize() {
        if (null == items){
            return 0;
        }
        return items.size();
    }
}
