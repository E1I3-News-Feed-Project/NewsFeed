package com.nbacm.newsfeed.domain.user.exception;

import com.nbacm.newsfeed.domain.exception.NotFoundException;

public class NickNameAlreadyExistsException extends NotFoundException {
    public NickNameAlreadyExistsException(String message) {
        super(message);
    }
}
