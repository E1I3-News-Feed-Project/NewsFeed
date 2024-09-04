package com.nbacm.newsfeed.domain.follow.exception;

import com.nbacm.newsfeed.domain.exception.UnauthorizedException;

public class UnauthorizedFollowRequestException extends UnauthorizedException {

    private static final String MESSAGE = "권한이 없습니다.";

    public UnauthorizedFollowRequestException() {super(MESSAGE);}

    public UnauthorizedFollowRequestException(String message) {super(message);}
}
