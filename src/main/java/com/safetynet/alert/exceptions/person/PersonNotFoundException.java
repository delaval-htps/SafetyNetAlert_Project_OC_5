package com.safetynet.alert.exceptions.person;

/**
 * Exception when a Object Person is not found in database.
 *
 * @author delaval
 *
 */
public class PersonNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor of Exception.
   *
   * @param message
   *            the String message for this exception.
   */
  public PersonNotFoundException(String message) {

    super(message);

  }

}
