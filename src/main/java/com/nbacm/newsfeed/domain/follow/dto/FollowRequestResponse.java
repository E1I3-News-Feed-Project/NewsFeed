package com.nbacm.newsfeed.domain.follow.dto;

import com.nbacm.newsfeed.domain.follow.entity.FollowRequest;
import com.nbacm.newsfeed.domain.follow.entity.FollowRequestStatus;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class FollowRequestResponse {
    private Long followRequestId;
    private FollowRequestStatus followRequestStatus;

    public static FollowRequestResponse from(FollowRequest followRequest) {
        return FollowRequestResponse.builder()
                .followRequestId(followRequest.getId())
                .followRequestStatus(followRequest.getStatus())
                .build();
    }
}
