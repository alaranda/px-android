package com.mercadolibre.dto.preference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InitPreferenceRequest {

  private final String callerId;
  private final String clientId;
  private final String prefId;
  private final String shortId;
  private final String flowId;
}
