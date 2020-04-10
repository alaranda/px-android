package com.mercadolibre.utils;

import com.mercadolibre.px.dto.lib.site.Site;

public class IfpeUtils {

  private static final boolean IFPE_ENABLED = false;

  public boolean isIfpeEnabled(final String siteId) {
    return Site.MLM.getSiteId().equalsIgnoreCase(siteId) && IFPE_ENABLED;
  }
}
