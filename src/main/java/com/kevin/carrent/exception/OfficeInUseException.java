package com.kevin.carrent.exception;

public class OfficeInUseException extends RuntimeException {

    public OfficeInUseException(String message) {
        super(message);
    }
}