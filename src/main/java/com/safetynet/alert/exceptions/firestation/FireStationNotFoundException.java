package com.safetynet.alert.exceptions.firestation;

/**
 * Exception when FireStation was not found.
 *
 * @author delaval
 *
 */
public class FireStationNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * constructor with a message of errors.
   *
   * @param message
   *        the message of errors.
   */
  public FireStationNotFoundException(String message) {

    super(message);

  }
}
