package com.mercadolibre.dto.congrats;

import com.mercadolibre.px.dto.lib.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExpenseSplit {
  private Text title;
  private Action action;
  private String imageUrl;
}
