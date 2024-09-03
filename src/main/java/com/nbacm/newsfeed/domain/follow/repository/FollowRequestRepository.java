package com.nbacm.newsfeed.domain.follow.repository;

import com.nbacm.newsfeed.domain.follow.entity.FollowRequest;
import com.nbacm.newsfeed.domain.follow.entity.FollowRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {
    Optional<FollowRequest> findBySenderUserIdAndReceiverUserIdAndStatus(Long senderId, Long receiverId, FollowRequestStatus status);

    // 특정 사용자가 받은 팔로우 요청들 중에서 특정 상태에 있는 것들을 조회할 때 사용
    List<FollowRequest> findByReceiverUserIdAndStatus(Long receiverId, FollowRequestStatus status);
}
