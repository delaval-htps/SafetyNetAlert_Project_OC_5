package com.safetynet.alert.exceptions;

import com.safetynet.alert.exceptions.firestation.FireStationAlreadyExistedException;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordAlreadyExistedException;
import com.safetynet.alert.exceptions.person.PersonAlreadyExistedException;

/**
 * Exception thrown by a bad request when an intance of entity is already existed in database.
 * this class is extended by following classes:
 * <ul>
 * <li>{@link PersonAlreadyExistedException}</li>
 * <li>{@link FireStationAlreadyExistedException}</li>
 * <li>{@link MedicalRecordAlreadyExistedException}</li>
 * </ul>
 *
 * @author delaval
 *
 */
public class AlReadyExistedException extends RuntimeException {


  private static final long serialVersionUID = 1043717316759774544L;

  /**
   * Constructor with a message.
   *
   * @param message error's message to display
   */
  public AlReadyExistedException(String message) {

    super(message);

  }


}
