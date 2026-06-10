package com.sportsclub.training.domain.exception;

public class AthleteNotFoundException extends DomainException {
    public AthleteNotFoundException(String message) {
        super(message);
    }

    public AthleteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
