package com.nbacm.newsfeed.domain.feed.exception;

public class FileDeletionException extends RuntimeException {
    public FileDeletionException(String message) {
        super(message);
    }

    public FileDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}