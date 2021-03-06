package com.safetynet.alert.exceptions.person;

import com.safetynet.alert.exceptions.AlReadyExistedException;

/**
 *Exception when trying to save a Person that already exists.
 *
 * @author delaval
 *
 */
public class PersonAlreadyExistedException extends AlReadyExistedException {

  private static final long serialVersionUID = 4082157772959731850L;

  /**
   * Constructor with message of errors.
   *
   * @param message
   *        a message of errors type String.
   */
  public PersonAlreadyExistedException(String message) {

    super(message);

  }
}
