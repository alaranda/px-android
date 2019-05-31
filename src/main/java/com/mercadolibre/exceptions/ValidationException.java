package com.mercadolibre.exceptions;

import org.apache.http.HttpStatus;

import javax.annotation.Nonnull;

/**
 * Exception que se lanza cuando falla la validacion
 */
public class ValidationException extends ApiException {

    public ValidationException(final @Nonnull String message) {
        super("bad_request", message, HttpStatus.SC_BAD_REQUEST);
    }
}
