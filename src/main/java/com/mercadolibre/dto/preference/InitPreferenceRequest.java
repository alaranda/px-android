package com.mercadolibre.dto.preference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InitPreferenceRequest {

  private String callerId;
  private String clientId;
  private String prefId;
  private String shortId;
}
