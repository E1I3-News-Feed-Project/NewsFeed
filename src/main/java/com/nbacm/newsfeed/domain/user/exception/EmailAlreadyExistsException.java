package com.nbacm.newsfeed.domain.user.exception;

import com.nbacm.newsfeed.domain.exception.ForbiddenException;

public class EmailAlreadyExistsException extends ForbiddenException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
