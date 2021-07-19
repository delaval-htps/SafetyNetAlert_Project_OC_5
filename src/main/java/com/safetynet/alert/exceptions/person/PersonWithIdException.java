package com.safetynet.alert.exceptions.person;

import com.safetynet.alert.exceptions.BadRequestException;

/**
 * Exception when in Body request there is a id for method post and put.
 *
 * @author delaval
 *
 */
public class PersonWithIdException extends BadRequestException {

  private static final long serialVersionUID = 7993643484906550449L;

  /**
   * constructor with message.
   *
   * @param message   a error's message to display when exception is thrown
   *
   */
  public PersonWithIdException(String message) {

    super(message);

  }

}
