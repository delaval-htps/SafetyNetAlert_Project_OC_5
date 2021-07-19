package com.safetynet.alert.exceptions.medicalrecord;

import com.safetynet.alert.exceptions.BadRequestException;

/**
 * exception thrown when there a Id in the body request of medicalRecord.
 *
 * @author delaval
 */
public class MedicalRecordWithIdException extends BadRequestException {

  private static final long serialVersionUID = 8922520029807272142L;

  /**
   * Constructor with a error's message.
   *
   * @param message   error's message to display in response body
   */
  public MedicalRecordWithIdException(String message) {

    super(message);

  }
}
