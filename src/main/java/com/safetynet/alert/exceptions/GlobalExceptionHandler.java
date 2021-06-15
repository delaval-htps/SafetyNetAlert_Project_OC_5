package com.safetynet.alert.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.safetynet.alert.exceptions.firestation.FireStationAllreadyMappedByAddressException;
import com.safetynet.alert.exceptions.firestation.FireStationAlreadyExistedException;
import com.safetynet.alert.exceptions.firestation.FireStationNotFoundException;
import com.safetynet.alert.exceptions.firestation.FireStationNotValidException;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordNotFoundException;
import com.safetynet.alert.exceptions.person.PersonAlreadyExistedException;
import com.safetynet.alert.exceptions.person.PersonChangedNamesException;
import com.safetynet.alert.exceptions.person.PersonNotFoundException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * A Controller Advice to manage all exceptions throws by controllers.
 *
 * @author delaval
 *
 */
@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerMedicalRecordNotFoundException(MedicalRecordNotFoundException exception,
          final HttpServletRequest request) {

    GlobalErrorResponse errorResponse = new GlobalErrorResponse();

    errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
    errorResponse.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
    errorResponse.setErrorMessage(exception.getMessage());
    errorResponse.setTimeStamp(new Date(System.currentTimeMillis()));

    log.error("Status:{} {} ; Request: {} {}; CauseBy:{} ",
        errorResponse.getStatus(),
        errorResponse.getError(),
        request.getMethod(),
        request.getRequestURI(),
        errorResponse.getErrorMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

  }

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
        errorResponse.getStatus(),
        errorResponse.getError(),
        request.getMethod(),
        request.getRequestURI(),
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
        errorResponse.getStatus(),
        errorResponse.getError(),
        request.getMethod(),
        request.getRequestURI(),
        errorResponse.getErrorMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

  }

  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerPersonAlreadyExistedException(PersonAlreadyExistedException exception,
          final HttpServletRequest request) {

    GlobalErrorResponse errorResponse = new GlobalErrorResponse();

    errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
    errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
    errorResponse.setErrorMessage(exception.getMessage());
    errorResponse.setTimeStamp(new Date(System.currentTimeMillis()));

    log.error("Status:{} {} ; Request: {} {}; CauseBy:{} ",
        errorResponse.getStatus(),
        errorResponse.getError(),
        request.getMethod(),
        request.getRequestURI(),
        errorResponse.getErrorMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

  }

  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerFireStationAlreadyExistedException(FireStationAlreadyExistedException exception,
          final HttpServletRequest request) {

    GlobalErrorResponse errorResponse = new GlobalErrorResponse();

    errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
    errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
    errorResponse.setErrorMessage(exception.getMessage());
    errorResponse.setTimeStamp(new Date(System.currentTimeMillis()));

    log.error("Status:{} {} ; Request: {} {}; CauseBy:{} ",
        errorResponse.getStatus(),
        errorResponse.getError(),
        request.getMethod(),
        request.getRequestURI(),
        errorResponse.getErrorMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

  }

  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerFireStationAllreadyMappedbyAddressException(
          FireStationAllreadyMappedByAddressException exception,
          final HttpServletRequest request) {

    GlobalErrorResponse errorResponse = new GlobalErrorResponse();

    errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
    errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
    errorResponse.setErrorMessage(exception.getMessage());
    errorResponse.setTimeStamp(new Date(System.currentTimeMillis()));

    log.error("Status:{} {} ; Request: {} {}; CauseBy:{} ",
        errorResponse.getStatus(),
        errorResponse.getError(),
        request.getMethod(),
        request.getRequestURI(),
        errorResponse.getErrorMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

  }

  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerFireStationNotValidException(
          FireStationNotValidException exception,
          final HttpServletRequest request) {

    GlobalErrorResponse errorResponse = new GlobalErrorResponse();

    errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
    errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
    errorResponse.setErrorMessage(exception.getMessage());
    errorResponse.setTimeStamp(new Date(System.currentTimeMillis()));

    log.error("Status:{} {} ; Request: {} {}; CauseBy:{} ",
        errorResponse.getStatus(),
        errorResponse.getError(),
        request.getMethod(),
        request.getRequestURI(),
        errorResponse.getErrorMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

  }

  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerFireStationNotFoundException(FireStationNotFoundException exception,
          final HttpServletRequest request) {

    GlobalErrorResponse errorResponse = new GlobalErrorResponse();

    errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
    errorResponse.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
    errorResponse.setErrorMessage(exception.getMessage());
    errorResponse.setTimeStamp(new Date(System.currentTimeMillis()));

    log.error("Status:{} {} ; Request: {} {}; CauseBy:{} ",
        errorResponse.getStatus(),
        errorResponse.getError(),
        request.getMethod(),
        request.getRequestURI(),
        errorResponse.getErrorMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

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
        errorResponse.getStatus(),
        errorResponse.getError(),
        request.getMethod(),
        request.getRequestURI(),
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

      errorResponse.addFieldError(error.getObjectName(),
          error.getField(),
          error.getDefaultMessage());

    }

    errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
    errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
    errorResponse.setErrorMessage("Error of validation");
    errorResponse.setTimeStamp(new Date(System.currentTimeMillis()));

    log.error("Status:{} {} ; Request: {} {} ; Message: {} ; FieldsValidationError {} ",
        errorResponse.getStatus(),
        errorResponse.getError(),
        request.getMethod(),
        request.getRequestURI(),
        errorResponse.getErrorMessage(),
        errorResponse.getFieldValidationErrors());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

  }

}
