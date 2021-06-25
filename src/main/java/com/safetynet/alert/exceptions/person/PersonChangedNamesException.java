package com.safetynet.alert.exceptions.person;

import com.safetynet.alert.exceptions.ChangedNamesException;

/**
 * Exception when trying to change FirstName and LastName of Person when updating it.
 *
 * @author delaval
 *
 */
public class PersonChangedNamesException extends ChangedNamesException {


  private static final long serialVersionUID = -8696223902084667819L;

  /**
   * Constructor with message of errors.
   *
   * @param message
   *        a message of errors type String.
   */
  public PersonChangedNamesException(String message) {

    super(message);

  }

}
