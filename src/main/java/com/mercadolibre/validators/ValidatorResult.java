package com.mercadolibre.validators;

import com.mercadolibre.px.exceptions.ValidationException;
import javax.annotation.Nonnull;

public class ValidatorResult {

  private boolean valid;
  private String message;

  public ValidatorResult(final boolean valid, @Nonnull final String message) {
    this.valid = valid;
    this.message = message;
  }

  public static ValidatorResult ok() {
    return new ValidatorResult(true, "ok");
  }

  public static ValidatorResult fail(@Nonnull final String message) {
    return new ValidatorResult(false, message);
  }

  public boolean isValid() {
    return valid;
  }

  public String getMessage() {
    return message;
  }

  public void throwIfInvalid() throws ValidationException {
    if (!isValid()) {
      throw new ValidationException(message);
    }
  }
}
