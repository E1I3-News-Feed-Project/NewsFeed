package com.nbacm.newsfeed.domain.follow.exception;

import com.nbacm.newsfeed.domain.exception.BadRequestException;

public class AlreadyFollowedException extends BadRequestException {

    private static final String MESSAGE = "이미 팔로우가 되어있습니다";

    public AlreadyFollowedException() {super(MESSAGE);}

    public AlreadyFollowedException(String message) {super(message);}
}
