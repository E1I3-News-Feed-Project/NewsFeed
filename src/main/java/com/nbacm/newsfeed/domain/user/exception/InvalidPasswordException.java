package com.nbacm.newsfeed.domain.user.exception;

import com.nbacm.newsfeed.domain.exception.NotFoundException;

public class InvalidPasswordException extends NotFoundException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
