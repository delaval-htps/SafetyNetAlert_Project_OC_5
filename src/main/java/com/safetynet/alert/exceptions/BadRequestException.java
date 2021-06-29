package com.safetynet.alert.exceptions;

/**
 * Global Exception thrown by controllers when given a bad or invalid request.
 * this class is extended by following classes:
 * <ul>
 * <li>{@link ChangedNamesException}</li>
 * <li>{@link AlreadyExistedException}</li>
 * </ul>
 *
 * @author delaval
 *
 */
public class BadRequestException extends RuntimeException {

  private static final long serialVersionUID = -5452601400681485968L;

  /**
   * constructor with a message.
   *
   * @param message
   *
   */
  public BadRequestException(String message) {

    super(message);

  }

}
