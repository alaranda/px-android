package com.mercadolibre.validators;

import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.px.toolkit.dto.Context;
import com.mercadolibre.utils.ErrorsConstants;


public class PreferencesValidator {

    /**
     * Valida los parametros de la Preference
     *
     * @param preference objeto con la preferencia de apgo
     * @throws ValidationException falla la validacion
     */
    public void validate(final Preference preference, final long callerId, final Context context) throws ValidationException {

        if (callerId == preference.getCollectorId()) {
            ValidatorResult.fail(ErrorsConstants.getInvalidPreferenceError(context.getLocale())).throwIfInvalid();
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

}
