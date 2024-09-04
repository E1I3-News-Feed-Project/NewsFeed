package com.nbacm.newsfeed.domain.follow.exception;

import com.nbacm.newsfeed.domain.exception.NotFoundException;

public class FollowRequestNotFoundException extends NotFoundException {

    private static final String MESSAGE = "해당 팔로우 신청을 찾을 수 없습니다.";

    public FollowRequestNotFoundException() {super(MESSAGE);}

    public FollowRequestNotFoundException(String message) {super(message);}
}
