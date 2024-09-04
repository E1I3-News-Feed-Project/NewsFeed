package com.nbacm.newsfeed.domain.user.exception;

import com.nbacm.newsfeed.domain.exception.NotFoundException;

public class NotMatchException extends NotFoundException {
    public NotMatchException(String message) {
        super(message);
    }
}
