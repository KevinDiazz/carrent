package com.kevin.carrent.exception;

public class ReservationAccessDeniedException extends RuntimeException {

    public ReservationAccessDeniedException(String message) {
        super(message);
    }
}