package com.safetynet.alert.exceptions.address;

import com.safetynet.alert.exceptions.NotFoundException;

/**
 * exception thrown when a address is not found.
 *
 * @author delaval
 *
 */
public class AddressNotFoundException extends NotFoundException {

  private static final long serialVersionUID = 2676479999672156689L;

  /**
   * Constructor for Exception with message.
   *
   * @param message   the error's message.
   *
   */
  public AddressNotFoundException(String message) {

    super(message);

  }
}
