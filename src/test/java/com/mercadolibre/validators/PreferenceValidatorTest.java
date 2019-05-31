package com.mercadolibre.validators;

import com.mercadolibre.dto.preference.Preference;
import com.mercadolibre.exceptions.ValidationException;
import com.mercadolibre.gson.GsonWrapper;
import org.apache.http.HttpStatus;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class PreferenceValidatorTest {

    private PreferencesValidator validator = new PreferencesValidator();

    @Test
    public void preferenceValidator_notShipments_validationSucceed() throws IOException, ValidationException {
        final Preference preference = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/preference/138275050-69faf356-c9b3-47d2-afe1-43d924fb6876.json")),
                Preference.class);
        validator.validate(preference);
    }

    @Test
    public void preferenceValidator_withShipments_validationFail() throws IOException, ValidationException {

        final Preference preference = GsonWrapper.fromJson(
                IOUtils.toString(getClass().getResourceAsStream("/preference/prefWithShipments.json")),
                Preference.class);
        try {
            validator.validate(preference);
            fail("Expected Validation Exception");
        } catch (ValidationException e) {
            assertThat(e.getCode(), is("bad_request"));
            assertThat(e.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
            assertThat(e.getDescription(), is("invalid pref"));
        }

    }

}
