package com.safetynet.alert.exceptions.firestation;

/**
 * Exception thrown when creation of FireStation when this already exists.
 *
 * @author delaval
 *
 */
public class FireStationAllreadyMappedByAddressException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor of the exception with a String message in parameter.
   *
   * @param  message
   *            the message of error.
   */
  public FireStationAllreadyMappedByAddressException(String message) {

    super(message);

  }
}
