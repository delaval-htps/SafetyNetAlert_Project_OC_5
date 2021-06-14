package com.safetynet.alert.exceptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Global error Response used in {@link GlobalExceptionHandler}
 * To format message of error to be be more readable for human.
 *
 * @author delaval
 *
 */
@NoArgsConstructor
@Getter
@Setter
public class GlobalErrorResponse {

  private Date timeStamp;
  private int status;
  private String error;
  private String errorMessage;
  private List<String> fieldValidationErrors = new ArrayList<>();

  /**
   * constructor with fields.
   *
   * @param date
   *        Date of creation of error Response.
   *
   * @param status
   *        Status of Response: to know if is bad, notfound... request.
   *
   * @param error
   *        the error of the exception thrown.
   *
   * @param errorMessage
   *         a message more explicit of the error.
   */
  public GlobalErrorResponse(Date date, int status, String error,
                             String errorMessage) {

    this.timeStamp = date;
    this.status = status;
    this.error = error;
    this.errorMessage = errorMessage;

  }

  /**
   * Method to add additional error's Fields with message
   * To precise where javax validations are not compatible.
   *
   * @param objectName
   *          Name of parsed object where validation's error is appeared.
   *
   * @param path
   *          the path for knowing the corrupt field.
   *
   * @param defaultMessage
   *          the default message.
   */
  public void addFieldError(String objectName,
      String path,
      String defaultMessage) {

    // FieldError error = new FieldError(objectName, path, defaultMessage);
    this.fieldValidationErrors.add("In " + objectName + " for field => " + path
        + " : " + defaultMessage);

  }



}
