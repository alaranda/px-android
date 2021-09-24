package com.mercadolibre.validators;

import static com.mercadolibre.utils.Translations.CANNOT_PAY_JUST_FOR_COLLECT;
import static com.mercadolibre.utils.Translations.CANNOT_PAY_WITH_LINK;

import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.exceptions.ValidationException;
import com.mercadolibre.utils.Translations;
import com.mercadolibre.utils.datadog.DatadogPreferencesMetric;

public class PreferencesValidator {

  private static final String PAYER_EQUALS_COLLECTOR = "payer_equals_collector";
  private static final String PAYER_DIFFERENT_FROM_CREATOR = "payer_different_from_creator";
  private static final String HAS_SHIPMENTS = "has_shipments";

  /**
   * Valida que el payerId sea distinto al collectorId y que la pref no tenga envios.
   *
   * @param context objeto con el contexto del request
   * @param preference objeto con la preferencia de pago
   * @param callerId id del payer
   * @throws ValidationException falla la validacion
   */
  public void validate(final Context context, final Preference preference, final String callerId)
      throws ValidationException {

    if (callerId.equals(preference.getCollectorId())) {
      DatadogPreferencesMetric.addInvalidPreferenceData(
          context, preference, PAYER_EQUALS_COLLECTOR);
      ValidatorResult.fail(
              Translations.INSTANCE.getTranslationByLocale(
                  context.getLocale(), CANNOT_PAY_JUST_FOR_COLLECT))
          .throwIfInvalid();
    }

    if (preference.getShipments() != null) {
      validateNullValue(preference.getShipments().getMode(), context, preference);
      validateNullValue(preference.getShipments().getDimensions(), context, preference);
    }
  }

  /**
   * Validacion que se ejecuta solamente cuando el flujo es pago de facturas de meli donde solamente
   * la puede pagar el creador de la preferencia.
   *
   * @param context objeto con el contexto del request
   * @param emailPayer email del payer
   * @param preference preferencia a pagar
   * @throws ValidationException falla la validacion
   */
  public void validatePayerDifferentThatPreferenceCreator(
      final Context context, final String emailPayer, final Preference preference)
      throws ValidationException {
    if (!preference.getPayer().getEmail().equalsIgnoreCase(emailPayer)) {
      DatadogPreferencesMetric.addInvalidPreferenceData(
          context, preference, PAYER_DIFFERENT_FROM_CREATOR);
      ValidatorResult.fail(
              Translations.INSTANCE.getTranslationByLocale(
                  context.getLocale(), CANNOT_PAY_WITH_LINK))
          .throwIfInvalid();
    }
  }

  private void validateNullValue(
      final String value, final Context context, final Preference preference)
      throws ValidationException {
    if (null != value) {
      DatadogPreferencesMetric.addInvalidPreferenceData(context, preference, HAS_SHIPMENTS);
      ValidatorResult.fail(
              Translations.INSTANCE.getTranslationByLocale(
                  context.getLocale(), CANNOT_PAY_WITH_LINK))
          .throwIfInvalid();
    }
  }
}
