package com.ratingsystem.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Integer id) {
        super("User was not found with id " + id);
    }
    public UserNotFoundException(String nickname) {
        super("User was not found with nickname/email " + nickname);
    }
}
