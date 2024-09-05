package com.nbacm.newsfeed.domain.user.exception;

import com.nbacm.newsfeed.domain.exception.ForbiddenException;

public class AlreadyDeletedException extends ForbiddenException {
    public AlreadyDeletedException(String message) {
        super(message);
    }
}
