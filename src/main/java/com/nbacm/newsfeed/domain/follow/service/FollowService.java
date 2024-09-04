package com.nbacm.newsfeed.domain.follow.service;

import com.nbacm.newsfeed.domain.follow.dto.FollowRequestResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface FollowService {

    void deleteFollow(Long followingId, String email);

    FollowRequestResponse sendFollowRequest(Long receiverId, String email);

    FollowRequestResponse acceptFollowRequest(Long requestId, String email);

    FollowRequestResponse rejectFollowRequest(Long requestId, String email);
}
