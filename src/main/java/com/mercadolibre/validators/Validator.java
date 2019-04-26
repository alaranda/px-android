package com.mercadolibre.validators;

/**
 * Interface will have a validate method which receives a parameter to be validated and returns the validation result
 */
@FunctionalInterface
public interface Validator<K> {

    ValidatorResult validate(K param);

}