package com.safetynet.alert.exceptions.medicalrecord;

import com.safetynet.alert.exceptions.ChangedNamesException;

/**
 * Exception when changing lastName and FirstName of a person in MedicalRecord.
 *
 * @author delaval
 *
 */
public class MedicalRecordChangedNamesException extends ChangedNamesException {

  private static final long serialVersionUID = -1414044193282665247L;

  /**
   * Constructor with message of errors.
   *
   * @param message
   *        a message of errors type String.
   */
  public MedicalRecordChangedNamesException(String message) {

    super(message);

  }
}
