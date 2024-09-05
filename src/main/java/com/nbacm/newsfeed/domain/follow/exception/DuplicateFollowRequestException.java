package com.nbacm.newsfeed.domain.follow.exception;

import com.nbacm.newsfeed.domain.exception.BadRequestException;

public class DuplicateFollowRequestException extends BadRequestException {

    private static final String MESSAGE = "중복된 팔로우 신청입니다.";

    public DuplicateFollowRequestException() {super(MESSAGE);}

    public DuplicateFollowRequestException(String message) {super(message);}
}

