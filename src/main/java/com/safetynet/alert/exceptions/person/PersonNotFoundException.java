package com.safetynet.alert.exceptions.person;

import com.safetynet.alert.exceptions.NotFoundException;

/**
 * Exception when a Object Person is not found in database.
 *
 * @author delaval
 *
 */
public class PersonNotFoundException extends NotFoundException {

  private static final long serialVersionUID = -8342888394839941340L;

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
