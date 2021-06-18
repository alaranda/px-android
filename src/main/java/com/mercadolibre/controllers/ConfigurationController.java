package com.mercadolibre.controllers;

import com.mercadolibre.service.ConfigurationService;
import java.util.Collections;
import java.util.Map;
import org.eclipse.jetty.http.HttpStatus;

public class ConfigurationController {

  public Map<String, Object> refreshConfig() {
    ConfigurationService.getInstance().refreshConfig();
    return Collections.singletonMap("status", HttpStatus.OK_200);
  }

  public Map<String, Object> getLoadedConfigs() {
    return ConfigurationService.getInstance().getProperties();
  }
}
