package com.mercadolibre.validators;

import com.mercadolibre.exceptions.ValidationException;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SimpleValidatorHelperTest {

    @Test
    public void isDoublePositive_isOk(){
        assertThat(SimpleValidatorHelper.isDoublePositive("monto").validate(2.5).isValid(), is(true));
    }

    @Test
    public void isDoublePositive_isNotOk(){
        try {
            SimpleValidatorHelper.isDoublePositive("monto").validate(-2.5).throwIfInvalid();
        } catch (ValidationException e) {
            assertThat(e.getMessage(), is("monto must be positive."));
        }

    }

}