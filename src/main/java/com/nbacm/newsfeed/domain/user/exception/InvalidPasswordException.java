package com.nbacm.newsfeed.domain.user.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super("message");
    }
}
