package com.nbacm.newsfeed.domain.follow.repository;

import com.nbacm.newsfeed.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerUserIdAndFollowingUserId(Long followerId, Long followingId);

    void deleteByFollowerUserIdAndFollowingUserId(Long followerId, Long followingId);

    List<Follow> findByFollowerUserId(Long followerId);
}
