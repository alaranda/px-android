package com.mercadolibre.dto.preference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PreferenceResponse {

  private final String prefId;
  private final String publicKey;
  private final String flowId;
  private final String productId;
  private final boolean escEnabled;
  private final boolean onetapEnabled;

  public String toLog(final PreferenceResponse preferenceResponse) {
    return new StringBuilder()
        .append(String.format("public_key: %s - ", preferenceResponse.getPublicKey()))
        .append(String.format("flow_id: %s - ", preferenceResponse.getFlowId()))
        .append(String.format("product_id: %s - ", preferenceResponse.getProductId()))
        .toString();
  }
}
