package com.kevin.carrent.exception;

public class LicensePlateAlreadyExistsException extends RuntimeException {

    public LicensePlateAlreadyExistsException(String message) {
        super(message);
    }
}