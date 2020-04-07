package com.mercadolibre.validators;

import static java.lang.String.format;

import java.math.BigDecimal;
import java.util.Objects;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

public final class SimpleValidatorHelper {

  private static String POSITIVE_ERROR_MSGE = "%s must be positive.";

  private SimpleValidatorHelper() {}

  /**
   * Valida que el valor String del param no sea null
   *
   * @param param nombre del parametro
   * @return validator
   */
  public static Validator<String> notNullString(final String param) {
    return SimpleValidator.from((s) -> !StringUtils.isBlank(s), format("%s is required.", param));
  }

  /**
   * Valida que el objeto no sea null
   *
   * @param param nombre del parametro
   * @return validator
   */
  static Validator<Object> notNull(final String param) {
    return SimpleValidator.from(Objects::nonNull, format("%s is required.", param));
  }

  /**
   * Valida que el valor del int param sea positivo
   *
   * @param param nombre del parametro
   * @return validator
   */
  public static Validator<Integer> isIntPositive(final String param) {
    return SimpleValidator.from(number -> number > 0, format(POSITIVE_ERROR_MSGE, param));
  }

  /**
   * Valida que el valor del double param sea positivo
   *
   * @param param nombre del parametro
   * @return validator
   */
  public static Validator<Double> isDoublePositive(final String param) {
    return SimpleValidator.from(number -> number > 0, format(POSITIVE_ERROR_MSGE, param));
  }

  /**
   * Valida que el valor del param sea un numero
   *
   * @param param nombre del parametro
   * @return validator
   */
  public static Validator<String> isNumber(final String param) {
    return SimpleValidator.from(
        (s) -> NumberUtils.isNumber(s), format("%s must be number.", param));
  }

  /**
   * Valida que el valor del big decimal param sea positivo
   *
   * @param param nombre del parametro
   * @return validator
   */
  static Validator<BigDecimal> isBigDecimalPositive(final String param) {
    return SimpleValidator.from(
        number -> number != null && number.signum() == 1, format(POSITIVE_ERROR_MSGE, param));
  }
}
