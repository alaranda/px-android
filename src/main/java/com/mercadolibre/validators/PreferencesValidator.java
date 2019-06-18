package com.mercadolibre.validators;

import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ValidationException;


public class PreferencesValidator {

    /**
     * Valida los parametros de la Preference
     *
     * @param preference objeto con la preferencia de apgo
     * @throws ValidationException falla la validacion
     */
    public void validate(final Preference preference) throws ValidationException {

        if (preference.getShipments() != null) {
            validateNullValue(preference.getShipments().getMode());
            validateNullValue(preference.getShipments().getDimensions());
        }
    }

    private void validateNullValue (String value) throws ValidationException {
        if (null != value) {
            ValidatorResult.fail("invalid pref").throwIfInvalid();
        }
    }
}
