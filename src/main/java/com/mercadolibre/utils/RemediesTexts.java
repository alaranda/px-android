package com.mercadolibre.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class RemediesTexts {

  private ResourceBundle getResourceBundle(final java.util.Locale locale) {
    return ResourceBundle.getBundle("Remedies", locale);
  }

  public String getTranslation(final Locale locale, final String key) {

    return getResourceBundle(getLocale(locale)).getString(key);
  }

  private Locale getLocale(Locale locale) {
    Locale localeToUse = locale;

    if (locale == null) {
      localeToUse = new Locale("DEFAULT");
    }

    return localeToUse;
  }
}
