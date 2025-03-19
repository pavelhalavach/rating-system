package com.ratingsystem.exception;

public class GameNotFoundException extends RuntimeException{
    public GameNotFoundException(Integer id) {
        super("Game was not found with id " + id);
    }


}
