package com.safetynet.alert.exceptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GlobalErrorResponse {

  private Date timeStamp;
  private int status;
  private String error;
  private String errorMessage;
  private List<String> fieldValidationErrors = new ArrayList<>();

  public GlobalErrorResponse(Date date, int status, String error,
                             String errorMessage) {
    this.timeStamp = date;
    this.status = status;
    this.error = error;
    this.errorMessage = errorMessage;
  }

  public void addFieldError(String objectName,
                            String path,
                            String defaultMessage) {
    // FieldError error = new FieldError(objectName, path, defaultMessage);
    this.fieldValidationErrors.add("In " + objectName + " for field => " + path
        + " : " + defaultMessage);
  }



}
