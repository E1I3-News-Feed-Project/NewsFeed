package com.nbacm.newsfeed.domain.user.exception;

public class AlreadyDeletedException extends RuntimeException{
    public AlreadyDeletedException(String message) {
        super(message);
    }
}
