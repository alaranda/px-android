package com.mercadolibre.validators;

import static com.mercadolibre.utils.Translations.CANNOT_PAY_JUST_FOR_COLLECT;
import static com.mercadolibre.utils.Translations.CANNOT_PAY_WITH_LINK;

import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.datadog.DatadogPreferencesMetric;

public class PreferencesValidator {

  /**
   * Valida que el payerId sea distinto al collectorId y que la pref no tenga envios.
   *
   * @param context objeto con el contexto del request
   * @param preference objeto con la preferencia de pago
   * @param callerId id del payer
   * @throws ValidationException falla la validacion
   */
  public void validate(final Context context, final Preference preference, final Long callerId)
      throws ValidationException {

    if (String.valueOf(callerId).equals(preference.getCollectorId())) {
      DatadogPreferencesMetric.addInvalidPreferenceData(preference, context);
      ValidatorResult.fail(
              Translations.INSTANCE.getTranslationByLocale(
                  context.getLocale(), CANNOT_PAY_JUST_FOR_COLLECT))
          .throwIfInvalid();
    }

    if (preference.getShipments() != null) {
      validateNullValue(preference.getShipments().getMode(), context);
      validateNullValue(preference.getShipments().getDimensions(), context);
    }
  }

  private void validateNullValue(String value, final Context context) throws ValidationException {
    if (null != value) {
      ValidatorResult.fail(
              Translations.INSTANCE.getTranslationByLocale(
                  context.getLocale(), CANNOT_PAY_WITH_LINK))
          .throwIfInvalid();
    }
  }

  /**
   * Validacion que se ejecuta solamente cuando el flujo es pago de facturas de meli donde solamente
   * la puede pagar el creador de la preferencia.
   *
   * @param context objeto con el contexto del request
   * @param emailPayer email del payer
   * @param emailPreference email de la pref
   * @throws ValidationException falla la validacion
   */
  public void isDifferent(
      final Context context, final String emailPayer, final String emailPreference)
      throws ValidationException {
    if (!emailPreference.equalsIgnoreCase(emailPayer)) {
      ValidatorResult.fail(
              Translations.INSTANCE.getTranslationByLocale(
                  context.getLocale(), CANNOT_PAY_WITH_LINK))
          .throwIfInvalid();
    }
  }
}
