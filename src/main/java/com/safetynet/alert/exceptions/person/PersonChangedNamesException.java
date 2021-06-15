package com.safetynet.alert.exceptions.person;

/**
 * Exception when trying to change FirstName and LastName of Person when updating it.
 *
 * @author delaval
 *
 */
public class PersonChangedNamesException extends RuntimeException {


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
