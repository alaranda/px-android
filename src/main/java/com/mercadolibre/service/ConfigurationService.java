package com.mercadolibre.service;

import com.mercadolibre.configuration.ConfigurationManager;
import com.mercadolibre.configuration.ConfigurationManagerBuilder;
import com.mercadolibre.px_config.Config;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationService {

  private static final ConfigurationService instance = null;

  public static ConfigurationService getInstance() {
    if (instance == null) {
      return new ConfigurationService();
    }
    return instance;
  }

  private final ConfigurationManager configurationManager;

  private ConfigurationService() {
    configurationManager = ConfigurationManagerBuilder.builder().build();
  }

  public void refreshConfig() {
    configurationManager.refresh();
  }

  public Map<String, Object> getProperties() {
    Map<String, Object> properties = new HashMap<>();
    configurationManager.getProperties().forEach(properties::put);
    return properties;
  }

  public String getStringByName(String nameKey) {
    return configurationManager.getStringProperty(nameKey).orElse(Config.getString(nameKey));
  }

  public Long getLongByName(String nameKey) {
    return configurationManager.getLongProperty(nameKey).orElse(Config.getLong(nameKey));
  }
}
