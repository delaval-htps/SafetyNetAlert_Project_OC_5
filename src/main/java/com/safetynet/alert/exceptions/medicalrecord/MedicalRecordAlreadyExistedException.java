package com.safetynet.alert.exceptions.medicalrecord;

/**
 * Exception when trying to save a already existed MedicalRecord.
 *
 * @author delaval
 *
 */
public class MedicalRecordAlreadyExistedException extends RuntimeException {

  private static final long serialVersionUID = 224373373119980673L;

  /**
   * constructor with a error's message.
   *
   * @param message
   *        the message of errors.
   */
  public MedicalRecordAlreadyExistedException(String message) {

    super(message);

  }
}
