package com.example.wakeupmate.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.warn(ex.getMessage(), ex);
        String message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(ExceptionCode.INVALID_REQUEST.getCode(), message));
    }

    @ExceptionHandler(StudyException.class)
    public ResponseEntity<ExceptionResponse> handleStudyException(StudyException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(PlaceException.class)
    public ResponseEntity<ExceptionResponse> handlePlaceException(PlaceException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(LocationException.class)
    public ResponseEntity<ExceptionResponse> handleLocationException(LocationException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionResponse> handleUserException(UserException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidJwtException(InvalidJwtException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        ExceptionCode.INTERNAL_SERVER_ERROR.getCode(),
                        ExceptionCode.INTERNAL_SERVER_ERROR.getMessage()
                ));
    }
} 