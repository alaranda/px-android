package com.mercadolibre.api;

import com.mercadolibre.px.api.lib.core.CurrencyDao;
import com.mercadolibre.px.api.lib.core.SiteDao;
import com.mercadolibre.px.api.lib.dto.ConfigurationCircuitBreaker;
import com.mercadolibre.px.api.lib.dto.ConfigurationDao;
import com.mercadolibre.px.api.lib.kyc.KycVaultV2Dao;
import com.mercadolibre.px_config.Config;

public class DaoProvider {

  private KycVaultV2Dao kycVaultV2Dao;
  private RiskApi riskApi;
  private SiteDao siteDao;
  private CurrencyDao currencyDao;

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

    kycVaultV2Dao = new KycVaultV2Dao(configurationDao, configurationCircuitBreaker);
    riskApi =
        new RiskApi(
            new ConfigurationDao(
                Integer.valueOf(Config.getInt("risk.socket.timeout")),
                Integer.valueOf(Config.getInt("risk.socket.timeout")),
                Integer.valueOf(Config.getInt("default.retries")),
                Integer.valueOf(Config.getInt("default.retry.delay")),
                Config.getString("risk.url.scheme"),
                Config.getString("risk.url.host")));
    this.currencyDao = new CurrencyDao(configurationDao);
    this.siteDao = new SiteDao(configurationDao, this.currencyDao);
  }

  public DaoProvider(final KycVaultV2Dao kycVaultV2Dao) {
    this.kycVaultV2Dao = kycVaultV2Dao;
  }

  public DaoProvider(final SiteDao siteDao) {
    this.siteDao = siteDao;
  }

  public KycVaultV2Dao getKycVaultV2Dao() {
    return kycVaultV2Dao;
  }

  public SiteDao getSiteDao() {
    return this.siteDao;
  }
}
