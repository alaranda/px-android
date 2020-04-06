package com.mercadolibre.validators;

import java.util.function.Predicate;

/**
 * Implements Validator, it evaluates a java Predicate and returns an ValidationException in case
 * the Predicate fails.
 */
public class SimpleValidator<K> implements Validator<K> {

  private Predicate<K> predicate;
  private String onErrorMessage;

  public static <K> SimpleValidator<K> from(Predicate<K> predicate, String onErrorMessage) {
    return new SimpleValidator<K>(predicate, onErrorMessage);
  }

  private SimpleValidator(Predicate<K> predicate, String onErrorMessage) {
    this.predicate = predicate;
    this.onErrorMessage = onErrorMessage;
  }

  @Override
  public ValidatorResult validate(K param) {
    return predicate.test(param) ? ValidatorResult.ok() : ValidatorResult.fail(onErrorMessage);
  }
}
