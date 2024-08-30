package com.nbacm.newsfeed.domain.Like.entity;

import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

public class PostLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postLikeId;

    @ManyToOne
    private User user;

    @ManyToOne
    private Feed feed;
}
