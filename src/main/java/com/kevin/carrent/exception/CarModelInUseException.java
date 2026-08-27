package com.kevin.carrent.exception;

public class CarModelInUseException extends RuntimeException {

    public CarModelInUseException(String message) {
        super(message);
    }
}