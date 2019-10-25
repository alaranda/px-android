package com.mercadolibre.dto.congrats;

import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.utils.CongratsTexts;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mercadolibre.utils.CongratsTexts.*;

public class Discounts {

    private String title;
    private String subtitle;
    private Action action;
    private ActionDownload actionDownload;
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
        private ActionDownload actionDownload;
        private Set<DiscountItem> items;

        public Builder(final Context context, final com.mercadolibre.dto.congrats.merch.Discounts discounts, final String platform, final String downloadUrl) {

            if (null == discounts) return;

            this.title = CongratsTexts.createTitleDiscount(context.getLocale());
            //this.subtitle =  CongratsTexts.createSubtitleDiscount(context.getLocale(), String.valueOf(discounts.getLoyaltyDiscounts()));
            this.subtitle = "";
            this.action = new Action(CongratsTexts.getTranslation(context.getLocale(), SEE_ALL), discounts.getLink());

            final Action action = new Action(CongratsTexts.getTranslation(context.getLocale(), DOWNLOAD), downloadUrl);
            this.actionDownload = new ActionDownload(CongratsTexts.getTranslationDownloadForApp(context.getLocale(), platform), action);
            Set<DiscountItem> items = new HashSet<>();

            if (null == discounts.getItems()) return;

            discounts.getItems().stream().map(item ->
                    items.add(new DiscountItem( item.getImage(), item.getTitle(), item.getSubtitle(), item.getLink(), item.getTrackingId())))
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
