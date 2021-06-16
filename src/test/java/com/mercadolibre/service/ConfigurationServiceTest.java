package com.mercadolibre.service;

import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationServiceTest {

  private ConfigurationService configurationService;

  @Before
  public void setup() {
    configurationService = ConfigurationService.getInstance();
  }

  @Test
  public void testGetAllProperties() {
    Map<String, Object> config = configurationService.getProperties();
    Assert.assertEquals(3, config.size());
  }

  @Test
  public void testRefreshConfig_shouldCallRefreshMethod() {
    configurationService.refreshConfig();
  }

  @Test
  public void testLongByName() {
    Assert.assertEquals(
        Long.valueOf(1000), configurationService.getLongByName("instructions.socket.timeout"));
  }

  @Test
  public void testStringByName() {
    Assert.assertEquals("/v1", configurationService.getStringByName("instructions.scope"));
  }
}
