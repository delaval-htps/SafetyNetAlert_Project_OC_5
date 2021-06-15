package com.safetynet.alert.exceptions.firestation;

/**
 * Exception when a FireStation doesn't exist.
 *
 * @author delaval
 *
 */
public class FireStationNotValidException extends RuntimeException {

  /**
   * Constructor with a error's message.
   *
   * @param message
   *        message of error.
   */
  public FireStationNotValidException(String message) {

    super(message);

  }
}
