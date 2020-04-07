package com.mercadolibre.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.mercadolibre.px.toolkit.exceptions.ValidationException;
import org.junit.Test;

public class ValidatorResultTest {

  @Test
  public void ifValidateIsOk_success() {
    ValidatorResult validator = ValidatorResult.ok();
    assertEquals(validator.isValid(), true);
  }

  @Test
  public void ifValidateIsNotOk_fails() {
    ValidatorResult validator = ValidatorResult.fail("this validation failed");
    assertEquals(validator.getMessage(), "this validation failed");
  }

  @Test
  public void ifValidateIsNotOkThenThrowsException() {
    ValidatorResult validator = ValidatorResult.fail("this validation failed");
    try {
      validator.throwIfInvalid();
      fail("Expected Validation Exception");
    } catch (ValidationException e) {
      assertEquals(e.getDescription(), "this validation failed");
    }
  }
}
