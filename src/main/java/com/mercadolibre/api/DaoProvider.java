package com.mercadolibre.api;

import com.mercadolibre.px.api.lib.dto.ConfigurationCircuitBreaker;
import com.mercadolibre.px.api.lib.dto.ConfigurationDao;
import com.mercadolibre.px.api.lib.kyc.KycVaultDao;
import com.mercadolibre.px.toolkit.config.Config;

public class DaoProvider {

  private KycVaultDao kycVaultDao;

  public DaoProvider() {

    ConfigurationCircuitBreaker configurationCircuitBreaker =
        new ConfigurationCircuitBreaker(
            Integer.valueOf(Config.getInt("default.breaker.interval")),
            Integer.valueOf(Config.getInt("default.breaker.buckets")),
            Integer.valueOf(Config.getInt("default.breaker.min.measure")));
    ConfigurationDao configurationDao =
        new ConfigurationDao(
            Integer.valueOf(Config.getInt("default.connection.timeout")),
            Integer.valueOf(Config.getInt("default.socket.timeout")),
            Integer.valueOf(Config.getInt("default.retries")),
            Integer.valueOf(Config.getInt("default.retry.delay")),
            Config.getString("api.base.url.scheme"),
            Config.getString("api.base.url.host"));

    kycVaultDao = new KycVaultDao(configurationDao, configurationCircuitBreaker);
  }

  public DaoProvider(final KycVaultDao kycVaultDao) {
    this.kycVaultDao = kycVaultDao;
  }

  public KycVaultDao getKycVaultDao() {
    return this.kycVaultDao;
  }
}
