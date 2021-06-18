package com.mercadolibre.endpoints;

import com.mercadolibre.controllers.ConfigurationController;
import org.junit.Test;

public class ConfigurationControllerTest {

  ConfigurationController configurationController = new ConfigurationController();

  @Test
  public void test_refreshConfig() {
    configurationController.refreshConfig();
  }

  @Test
  public void test_getLoadedConfigs() {
    configurationController.getLoadedConfigs();
  }
}
