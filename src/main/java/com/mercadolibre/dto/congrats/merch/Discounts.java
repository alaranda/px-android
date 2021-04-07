package com.mercadolibre.dto.congrats.merch;

import java.util.List;
import lombok.Getter;

@Getter
public class Discounts {

  private String link;
  private int loyaltyDiscounts;
  private int totalDiscounts;
  private List<Item> items;
  private TouchpointData touchpoint;
  private String title;
}
