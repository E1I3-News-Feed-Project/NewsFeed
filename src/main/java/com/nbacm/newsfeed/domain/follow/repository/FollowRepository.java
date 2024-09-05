package com.nbacm.newsfeed.domain.follow.repository;

import com.nbacm.newsfeed.domain.follow.entity.Follow;
import com.nbacm.newsfeed.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerUserIdAndFollowingUserId(Long followerId, Long followingId);

    void deleteByFollowerUserIdAndFollowingUserId(Long followerId, Long followingId);

    List<Follow> findByFollowerUserId(Long followerId);

    @Query("SELECT f.following FROM Follow f WHERE f.follower = :follower")
    List<User> findFollowedUsersByFollower(@Param("follower") User follower);
}