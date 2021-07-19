package com.safetynet.alert.exceptions.firestation;

import com.safetynet.alert.exceptions.BadRequestException;

/**
 * Exception thrown when there is a idFireStation in body request.
 *
 * @author delaval
 *
 */
public class FireStationWithIdException extends BadRequestException {

  private static final long serialVersionUID = -458419886331514055L;

  /**
   * constructor with message.
   *
   * @param message error's message to display in response
   */
  public FireStationWithIdException(String message) {

    super(message);

  }
}
