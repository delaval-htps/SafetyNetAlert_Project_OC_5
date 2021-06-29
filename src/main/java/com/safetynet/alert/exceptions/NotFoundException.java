package com.safetynet.alert.exceptions;

import com.safetynet.alert.exceptions.address.AddressNotFoundException;
import com.safetynet.alert.exceptions.firestation.FireStationNotFoundException;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordNotFoundException;
import com.safetynet.alert.exceptions.person.PersonNotFoundException;

/**
 * Global Exception when searching an instance of entity or an Object is not found.
 * this class is extended by following class:
 * <ul>
 * <li>{@link PersonNotFoundException}</li>
 * <li>{@link FireStationNotFoundException}</li>
 * <li>{@link MedicalRecordNotFoundException}</li>
 * <li>{@link AddressNotFoundException}</li>
 * </ul>
 *
 * @author delaval
 *
 */
public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = -8967077527824336146L;

  /**
   * Constructor with message.
   *
   * @param message   error's message to display
   *
   */
  public NotFoundException(String message) {

    super(message);

  }

}
