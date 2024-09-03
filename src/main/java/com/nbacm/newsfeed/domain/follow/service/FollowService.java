package com.nbacm.newsfeed.domain.follow.service;

import com.nbacm.newsfeed.domain.follow.dto.FollowRequestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface FollowService {

    void deleteFollow(Long followingId, HttpServletRequest request);

    FollowRequestResponse sendFollowRequest(Long receiverId, HttpServletRequest request);

    FollowRequestResponse acceptFollowRequest(Long requestId, HttpServletRequest request);

    FollowRequestResponse rejectFollowRequest(Long requestId, HttpServletRequest request);
}
