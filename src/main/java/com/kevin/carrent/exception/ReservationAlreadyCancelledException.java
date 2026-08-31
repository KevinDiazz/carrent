package com.kevin.carrent.exception;

public class ReservationAlreadyCancelledException extends RuntimeException {

    public ReservationAlreadyCancelledException(String message) {
        super(message);
    }
}