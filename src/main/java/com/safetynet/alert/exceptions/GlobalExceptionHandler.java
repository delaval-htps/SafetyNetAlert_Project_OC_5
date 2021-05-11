package com.safetynet.alert.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.safetynet.alert.exceptions.person.PersonChangedNamesException;
import com.safetynet.alert.exceptions.person.PersonNotFoundException;
import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerPersonNotFoundException(PersonNotFoundException exception,
                                     final HttpServletRequest request) {

    GlobalErrorResponse errorResponse = new GlobalErrorResponse();

    errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
    errorResponse.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
    errorResponse.setErrorMessage(exception.getMessage());
    errorResponse.setTimeStamp(new Date(System.currentTimeMillis()));

    log.error("Status:{} {} ; Request: {} {}; CauseBy:{} ",
              errorResponse.getStatus(), errorResponse.getError(),
              request.getMethod(), request.getRequestURI(),
              errorResponse.getErrorMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

  }

  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerPersonChangedNamesException(PersonChangedNamesException exception,
                                         final HttpServletRequest request) {

    GlobalErrorResponse errorResponse = new GlobalErrorResponse();

    errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
    errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
    errorResponse.setErrorMessage(exception.getMessage());
    errorResponse.setTimeStamp(new Date(System.currentTimeMillis()));

    log.error("Status:{} {} ; Request: {} {}; CauseBy:{} ",
              errorResponse.getStatus(), errorResponse.getError(),
              request.getMethod(), request.getRequestURI(),
              errorResponse.getErrorMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

  }

  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerGlobalException(Exception exception,
                             final HttpServletRequest request) {

    GlobalErrorResponse errorResponse = new GlobalErrorResponse();

    errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
    errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
    errorResponse.setErrorMessage(exception.getMessage());
    errorResponse.setTimeStamp(new Date(System.currentTimeMillis()));
    log.error("Status:{} {} ; Request: {} {}; CauseBy:{} ",
              errorResponse.getStatus(), errorResponse.getError(),
              request.getMethod(), request.getRequestURI(),
              errorResponse.getErrorMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

  }

  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                             final HttpServletRequest request)
          throws JsonProcessingException {

    GlobalErrorResponse errorResponse = new GlobalErrorResponse();


    for (FieldError error : exception.getBindingResult().getFieldErrors()) {
      errorResponse.addFieldError(error.getObjectName(), error.getField(),
                                  error.getDefaultMessage());
    }

    errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
    errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
    errorResponse.setErrorMessage("Error of validation");
    errorResponse.setTimeStamp(new Date(System.currentTimeMillis()));

    log.error("Status:{} {} ; Request: {} {} ; Message: {} ; FieldsValidationError {} ",
              errorResponse.getStatus(), errorResponse.getError(),
              request.getMethod(), request.getRequestURI(),
              errorResponse.getErrorMessage(),
              errorResponse.getFieldValidationErrors());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

  }

}
