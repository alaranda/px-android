package com.mercadolibre.validators;

import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.utils.ErrorsConstants;
import com.mercadolibre.utils.datadog.DatadogPreferencesMetric;


public class PreferencesValidator {

    /**
     * Valida que el payerId sea distinto al collectorId y que la pref no tenga envios.
     *
     * @param  context objeto con el contexto del request
     * @param preference objeto con la preferencia de pago
     * @param callerId id del payer
     * @throws ValidationException falla la validacion
     */
    public void validate( final Context context, final Preference preference, final long callerId) throws ValidationException {

        if (callerId == preference.getCollectorId()) {
            DatadogPreferencesMetric.addInvalidPreferenceData(preference);
            ValidatorResult.fail(ErrorsConstants.getPayerEqualsCollectorError(context.getLocale())).throwIfInvalid();
        }

        if (preference.getShipments() != null) {
            validateNullValue(preference.getShipments().getMode(), context);
            validateNullValue(preference.getShipments().getDimensions(), context);
        }
    }

    private void validateNullValue (String value, final Context context) throws ValidationException {
        if (null != value) {
            ValidatorResult.fail(ErrorsConstants.getInvalidPreferenceError(context.getLocale())).throwIfInvalid();
        }
    }

    /**
     * Validacion que se ejecuta solamente cuando el flujo es pago de facturas de meli donde solamente la puede pagar
     * el creador de la preferencia.
     *
     * @param  context objeto con el contexto del request
     * @param emailPayer email del payer
     * @param emailPreference email de la pref
     * @throws ValidationException falla la validacion
     */
    public void isDifferent(final Context context, final String emailPayer, final String emailPreference) {
        if (!emailPreference.equalsIgnoreCase(emailPayer)){
            ValidatorResult.fail(ErrorsConstants.getInvalidPreferenceError(context.getLocale())).throwIfInvalid();
        }
    }

}
