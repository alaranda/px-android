package com.mercadolibre.endpoints;

import static org.mockito.Mockito.mock;

import com.mercadolibre.controllers.ConfigurationController;
import com.mercadolibre.service.ConfigurationService;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationControllerTest {

  ConfigurationController configurationController;
  ConfigurationService configurationServiceMock;

  @Before
  public void setUp() {
    configurationServiceMock = mock(ConfigurationService.class);
  }

  @Test
  public void test_refreshConfig() {
    configurationController.refreshConfig();
  }

  @Test
  public void test_getLoadedConfigs() {
    configurationController.getLoadedConfigs();
  }
}
