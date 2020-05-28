package com.mercadolibre.dto.congrats.merch;

import java.util.List;

public class Discounts {

  private String link;
  private int loyaltyDiscounts;
  private int totalDiscounts;
  private List<Item> items;
  private TouchpointData touchpoint;

  public String getLink() {
    return link;
  }

  public List<Item> getItems() {
    return items;
  }

  public int getLoyaltyDiscounts() {
    return loyaltyDiscounts;
  }

  public int getTotalDiscounts() {
    return totalDiscounts;
  }

  public TouchpointData getTouchpoint() {
    return touchpoint;
  }
}
