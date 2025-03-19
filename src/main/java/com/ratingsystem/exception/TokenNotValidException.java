package com.ratingsystem.exception;

public class TokenNotValidException extends RuntimeException{
    public TokenNotValidException(String token) {
        super("Provided token " + token + " is not valid");
    }
}
