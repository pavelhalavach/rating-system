package com.ratingsystem.exception;

public class EmailAlreadyTakenException extends RuntimeException {
    public EmailAlreadyTakenException(String email) {
        super("The email " + email + " is already used for registration");
    }
}
