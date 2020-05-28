package com.mercadolibre.dto.congrats;

import static com.mercadolibre.utils.Translations.DISCOUNTS;
import static com.mercadolibre.utils.Translations.DISCOUNTS_DOWNLOAD_ML;
import static com.mercadolibre.utils.Translations.DISCOUNTS_DOWNLOAD_MP;
import static com.mercadolibre.utils.Translations.DOWNLOAD;
import static com.mercadolibre.utils.Translations.SEE_ALL;

import com.mercadolibre.dto.congrats.merch.TouchpointData;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.utils.Translations;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Discounts {

  private String title;
  private String subtitle;
  private Action action;
  private ActionDownload actionDownload;
  private TouchpointData touchpoint;
  private Set<DiscountItem> items;

  public Discounts(final Builder builder) {
    this.title = builder.title;
    this.subtitle = builder.subtitle;
    this.action = builder.action;
    this.actionDownload = builder.actionDownload;
    this.items = builder.items;
    this.touchpoint = builder.touchpoint;
  }

  public static class Builder {

    private String title;
    private String subtitle;
    private Action action;
    private ActionDownload actionDownload;
    private Set<DiscountItem> items;
    private TouchpointData touchpoint;

    public Builder(
        final Context context,
        final com.mercadolibre.dto.congrats.merch.Discounts discounts,
        final String platform,
        final String downloadUrl) {

      if (null == discounts) return;

      this.title = Translations.INSTANCE.getTranslationByLocale(context.getLocale(), DISCOUNTS);
      this.subtitle = "";

      this.action =
          new Action(
              Translations.INSTANCE.getTranslationByLocale(context.getLocale(), SEE_ALL),
              discounts.getLink());

      final Action action =
          new Action(
              Translations.INSTANCE.getTranslationByLocale(context.getLocale(), DOWNLOAD),
              downloadUrl);

      String downloadKey =
          Translations.INSTANCE.getTranslationByLocale(context.getLocale(), DISCOUNTS_DOWNLOAD_MP);
      if (platform.equalsIgnoreCase("MP")) {
        downloadKey =
            Translations.INSTANCE.getTranslationByLocale(
                context.getLocale(), DISCOUNTS_DOWNLOAD_ML);
      }

      this.actionDownload = new ActionDownload(downloadKey, action);
      Set<DiscountItem> items = new HashSet<>();

      if (null == discounts.getItems()) return;

      discounts.getItems().stream()
          .map(
              item ->
                  items.add(
                      new DiscountItem(
                          item.getImage(),
                          item.getTitle(),
                          item.getSubtitle(),
                          item.getLink(),
                          item.getTrackingId())))
          .collect(Collectors.toSet());

      this.items = items;
      this.touchpoint = discounts.getTouchpoint();
    }

    public Discounts build() {
      return new Discounts(this);
    }
  }

  public int discountsSize() {
    if (null == items) {
      return 0;
    }
    return items.size();
  }

  public String toString() {
    return String.format(
        "Discounts{Title=[%s], Subtitle=[%s], Action=[%s], ActionDownload=[%s]}",
        title, subtitle, action.toString(), actionDownload.toString());
  }
}
