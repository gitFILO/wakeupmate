package com.example.wakeupmate.common.exception;

import lombok.Getter;

@Getter
public class PlaceException extends RuntimeException {

    private final int code;
    private final String message;

    public PlaceException(ExceptionCode exceptionCode) {
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }
} 