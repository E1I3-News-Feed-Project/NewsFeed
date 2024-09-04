package com.nbacm.newsfeed.domain.user.exception;

import com.nbacm.newsfeed.domain.exception.ForbiddenException;
import com.nbacm.newsfeed.domain.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
