package com.safetynet.alert.exceptions.firestation;

import com.safetynet.alert.exceptions.NotFoundException;

/**
 * Exception when FireStation was not found.
 *
 * @author delaval
 *
 */
public class FireStationNotFoundException extends NotFoundException {

  private static final long serialVersionUID = -61495583706504600L;

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
