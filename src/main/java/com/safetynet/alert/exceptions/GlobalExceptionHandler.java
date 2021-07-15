package com.safetynet.alert.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
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
  /**
   * Handler for {@link NotFoundException} .
   * Allows to manage the error throw by this exception and to return
   * a {@link GlobalErrorResponse} more adapted to read.
   *
   * @param exception the NotfoundException thrown.
   *
   * @param request   the request given to a controller.
   *
   * @return  a ResponseEntity with a {@link GlobalErrorResponse} specified for this exception
   *             and a {@link HttpStatus} of Not Found.
   */
  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerNotFoundException(NotFoundException exception,
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

  /**
   * Handler for {@link BadRequestException} .
   * Allows to manage the error throw by this exception and to return
   * a {@link GlobalErrorResponse} more adapted to read.
   *
   * @param exception the BadRequestException thrown by controller.
   *
   * @param request   the request given to a controller.
   *
   * @return  a ResponseEntity with a {@link GlobalErrorResponse} specified for this exception
   *            and a {@link HttpStatus} of Bad Request.
   */

  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerBadRequestException(BadRequestException exception,
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

  /**
   * Handler for global {@link Exception} .
   * Allows to manage the error throw by this exception and to return
   * a {@link GlobalErrorResponse} more adapted to read.
   *
   * @param exception the BadRequestException thrown by controller.
   *
   * @param request   the request given to a controller.
   *
   * @return  a ResponseEntity with a {@link GlobalErrorResponse} specified for this exception
   *            and a {@link HttpStatus} of Bad Request.
   */
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

  /**
   * Handler for global {@link MethodArgumentNotValidException} .
   * Allows to manage the error throw by this exception and to return
   * a {@link GlobalErrorResponse} more adapted to read.
   *
   * @param exception the {@link MethodArgumentNotValidException} thrown by controller
   *                    when hibernate validation fails.
   *
   * @param request   the request given to a controller.
   *
   * @return  a ResponseEntity with a {@link GlobalErrorResponse} specified for this exception
   *            and a {@link HttpStatus} of Bad Request. NB: this GlobalErrorResponse contains
   *            a FieldsValidationError to allows to find which field of the
   *            request is not valid
   */
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

  @ExceptionHandler
  public ResponseEntity<GlobalErrorResponse>
      handlerMethodArgumentNotValidException(ConstraintViolationException exception,
          final HttpServletRequest request)
          throws JsonProcessingException {

    GlobalErrorResponse errorResponse = new GlobalErrorResponse();


    for (ConstraintViolation<?> contraint : exception.getConstraintViolations()) {

      errorResponse.addFieldError(

          contraint.getPropertyPath().toString(),
          contraint.getMessage());
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
