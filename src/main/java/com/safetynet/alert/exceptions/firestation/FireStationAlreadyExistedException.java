package com.safetynet.alert.exceptions.firestation;

import com.safetynet.alert.exceptions.AlReadyExistedException;

/**
 * Exception when saving a FireStation that already exists.
 *
 * @author delaval
 *
 */
public class FireStationAlreadyExistedException extends AlReadyExistedException {

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
