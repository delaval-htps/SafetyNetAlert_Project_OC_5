package com.safetynet.alert.exceptions;

import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordChangedNamesException;
import com.safetynet.alert.exceptions.person.PersonChangedNamesException;

/**
 * Global Exception thrown by controllers when
 * lastName and FirstName are changed when updating or creating a entity.
 * This Class is extended by following classes:
 * <ul>
 * <li>{@link PersonChangedNamesException}</li>
 * <li>{@link MedicalRecordChangedNamesException}</li>
 * </ul>
 *
 * @author delaval
 *
 */
public class ChangedNamesException extends RuntimeException {

  private static final long serialVersionUID = 2710002378027246386L;

  /**
   * constructor with a error's message.
   *
   * @param message error's message to display.
   */
  public ChangedNamesException(String message) {

    super(message);

  }
}
