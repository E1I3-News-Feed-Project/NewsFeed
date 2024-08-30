package com.nbacm.newsfeed.domain.Like.entity;

import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.comment.entity.Comment;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

public class CommentLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentLikeId;

    @ManyToOne
    private Comment comment;

    @ManyToOne
    private User user;
}
