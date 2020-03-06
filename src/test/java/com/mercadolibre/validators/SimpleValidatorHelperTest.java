package com.mercadolibre.validators;

import com.mercadolibre.px.dto.lib.preference.Preference;
import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class SimpleValidatorHelperTest {

    @Test
    public void ifParamIsDoublePositive_success(){
        assertThat(SimpleValidatorHelper.isDoublePositive("monto").validate(2.5).isValid(), is(true));
    }

    @Test
    public void ifParamIsNotDoublePositiveThenThrowsException(){
        try {
            SimpleValidatorHelper.isDoublePositive("monto").validate(-2.5).throwIfInvalid();
        } catch (ValidationException e) {
            assertThat(e.getDescription(), is("monto must be positive."));
        }
    }

    @Test
    public void ifParamIsNullStringThenThrowsException(){
        try {
            SimpleValidatorHelper.notNullString("param").validate(null).throwIfInvalid();
            fail("Expected Validation Exception");
        } catch (ValidationException e) {
            assertThat(e.getDescription(), is("param is required."));
        }
    }

    @Test
    public void ifParamIsNotNullString_success(){
        assertThat(SimpleValidatorHelper.notNullString("param").validate("paramStringOK").isValid(), is(true));
    }

    @Test
    public void ifParamIsNullThenThrowsException(){
        try {
            SimpleValidatorHelper.notNull("param").validate(null).throwIfInvalid();
            fail("Expected Validation Exception");
        } catch (ValidationException e) {
            assertThat(e.getDescription(), is("param is required."));
        }
    }

    @Test
    public void ifParamIsNotNull_success(){
        assertThat(SimpleValidatorHelper.notNull("param").validate(Mockito.mock(Preference.class)).isValid(), is(true));
    }

    @Test
    public void ifIsBigDecimalPositive_success(){
        assertThat(SimpleValidatorHelper.isBigDecimalPositive("param").validate(new BigDecimal(11111)).isValid(), is(true));
    }

    @Test
    public void ifIsNotBigDecimalPositiveThenThrowsException(){
        try {
            SimpleValidatorHelper.isBigDecimalPositive("param").validate(new BigDecimal(-1)).throwIfInvalid();
            fail("Expected Validation Exception");
        } catch (ValidationException e) {
            assertThat(e.getDescription(), is("param must be positive."));
        }
    }

}