package com.mercadolibre.dto.cha;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CardHolder {

  private CardHolderData data;
  private final String deviceChannel = "APP";
  private final String protocol = "3DS";
  private Long userID;
  private final String version = "2.0";
}
