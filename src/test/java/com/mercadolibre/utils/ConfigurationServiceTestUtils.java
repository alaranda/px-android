package com.mercadolibre.utils;

public class ConfigurationServiceTestUtils {

  public static void setupPropertiesForLocal() {
    if (System.getenv("SCOPE") == null) {
      System.setProperty("configFileName", "src/main/resources/configuration.properties");
      System.setProperty("checksumEnabled", "false");
    }
  }
}
