package com.safetynet.alert.exceptions.firestation;

/**
 * Exception when a FireStation doesn't exist.
 *
 * @author delaval
 *
 */
public class FireStationNotValidException extends RuntimeException {

  private static final long serialVersionUID = -4777181917060389629L;

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
