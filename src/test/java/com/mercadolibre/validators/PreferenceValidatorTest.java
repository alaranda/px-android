package com.mercadolibre.validators;

import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.gson.GsonWrapper;
import com.mercadolibre.utils.ContextUtilsTestHelper;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class PreferenceValidatorTest {

    private PreferencesValidator validator = new PreferencesValidator();

    private static final long CALLER_ID_VALID = Long.valueOf(11111);
    private static final long CALLER_ID_INVALID = Long.valueOf(22222);

    @Test
    public void preferenceValidator_notShipments_validationSucceed() throws IOException, ValidationException {

        final Preference preference = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-69faf356-c9b3-47d2-afe1-43d924fb6876.json")),
                Preference.class);
        validator.validate(preference, CALLER_ID_VALID, ContextUtilsTestHelper.CONTEXT_ES);
    }

    @Test
    public void preferenceValidator_withShipments_validationFail() throws IOException, ValidationException {

        final long calllerId = Long.valueOf(11111);
        final Preference preference = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/preference/prefWithShipments.json")),
                Preference.class);
        try {
            validator.validate(preference, calllerId, ContextUtilsTestHelper.CONTEXT_ES);
            fail("Expected Validation Exception");
        } catch (ValidationException e) {
            assertThat(e.getMessage(), is("No puedes pagar con este link de pago. "));
        }

    }

}
