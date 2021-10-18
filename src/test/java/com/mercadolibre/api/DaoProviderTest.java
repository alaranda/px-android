package com.mercadolibre.api;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.mercadolibre.px.api.lib.core.SiteDao;
import com.mercadolibre.px.api.lib.kyc.KycVaultV2Dao;
import org.junit.Test;

public class DaoProviderTest {

  @Test
  public void testGetKycVaultV2Dao_fromDefaultConstructor() {
    final DaoProvider daoProvider = new DaoProvider();
    assertNotNull(daoProvider.getKycVaultV2Dao());
    assertNotNull(daoProvider.getSiteDao());
  }

  @Test
  public void testGetKycVaultV2Dao_fromCustomConstructor() {
    KycVaultV2Dao kycVaultV2Dao = mock(KycVaultV2Dao.class);
    final DaoProvider daoProvider = new DaoProvider(kycVaultV2Dao);
    assertEquals(kycVaultV2Dao, daoProvider.getKycVaultV2Dao());
  }

  @Test
  public void testGetSiteDao_fromCustomConstructor() {
    final SiteDao siteDao = mock(SiteDao.class);
    final DaoProvider daoProvider = new DaoProvider(siteDao);
    assertEquals(siteDao, daoProvider.getSiteDao());
  }
}
