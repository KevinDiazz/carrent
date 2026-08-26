package com.kevin.carrent.exception;

public class OfficeNotFoundException extends RuntimeException {

    public OfficeNotFoundException(String message) {
        super(message);
    }
}