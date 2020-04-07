package com.mercadolibre.exceptions;

public final class UnsuccessfulResponseException extends RuntimeException {

  /**
   * UnsuccessfulResponseException constructor
   *
   * @param message the message
   */
  public UnsuccessfulResponseException(final String message) {
    super(message);
  }
}
