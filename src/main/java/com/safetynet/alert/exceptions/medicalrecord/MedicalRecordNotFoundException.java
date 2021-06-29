package com.safetynet.alert.exceptions.medicalrecord;

import com.safetynet.alert.exceptions.NotFoundException;

/**
 * exception when trying to found a MedicalRecord that doesn't exist.
 *
 * @author delaval
 *
 */
public class MedicalRecordNotFoundException extends NotFoundException {

  private static final long serialVersionUID = -1376830826254629814L;

  /**
   * Constructor with message of errors.
   *
   * @param message
   *        a message of errors type String.
   */
  public MedicalRecordNotFoundException(String message) {

    super(message);

  }
}
