package com.example.wakeupmate.common.exception;

import lombok.Getter;

@Getter
public class LocationException extends RuntimeException {

    private final int code;
    private final String message;

    public LocationException(ExceptionCode exceptionCode) {
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }
} 