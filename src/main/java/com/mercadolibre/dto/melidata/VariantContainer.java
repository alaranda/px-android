package com.mercadolibre.dto.melidata;

import com.mercadolibre.melidata.experiments.ExperimentConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VariantContainer {
  private final String experimentId;
  private final String experimentName;
  private final String variantId;
  private final String variantName;
  private final ExperimentConfiguration experimentConfiguration;

  public String getConfigAsString(String key, String defaultValue) {
    return experimentConfiguration.getConfigAsString(key, defaultValue);
  }

  public String getConfigAsString(String key) {
    return experimentConfiguration.getConfigAsString(key);
  }

  public Object getConfig(String key, Object defaultValue) {
    return experimentConfiguration.getConfig(key, defaultValue);
  }

  public Object getConfig(String key) {
    return experimentConfiguration.getConfig(key);
  }
}
