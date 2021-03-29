package com.mercadolibre.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Item {
  private String id;
  private String title;
  private String description;
  private String pictureUrl;
  private String categoryId;
  private Integer quantity;
  private BigDecimal unitPrice;
}
