package com.nbacm.newsfeed.domain.follow.exception;

import com.nbacm.newsfeed.domain.exception.BadRequestException;

public class SelfFollowException extends BadRequestException {

    private static final String MESSAGE = "자신에게 팔로우 신청을 할 수 없습니다..";

    public SelfFollowException() {super(MESSAGE);}

    public SelfFollowException(String message) {super(message);}
}
