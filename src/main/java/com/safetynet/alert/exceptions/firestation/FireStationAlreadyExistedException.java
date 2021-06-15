package com.safetynet.alert.exceptions.firestation;

/**
 * Exception when saving a FireStation that already exists.
 *
 * @author delaval
 *
 */
public class FireStationAlreadyExistedException extends RuntimeException {

  private static final long serialVersionUID = -5252437157960736934L;

  /**
   * Constructor with message of errors.
   *
   * @param message
   *        a message of errors type String.
   */
  public FireStationAlreadyExistedException(String message) {

    super(message);

  }
}
