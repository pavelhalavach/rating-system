package com.ratingsystem.exception;

public class NicknameAlreadyTakenException extends RuntimeException {
    public NicknameAlreadyTakenException(String nickname) {
        super("The nickname " + nickname + " is already used for registration");
    }
}
