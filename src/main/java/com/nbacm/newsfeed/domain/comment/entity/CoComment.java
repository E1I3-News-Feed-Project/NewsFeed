package com.nbacm.newsfeed.domain.comment.entity;

import com.nbacm.newsfeed.domain.comment.dto.request.CoCommentRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.time.entity.BaseTime;
import com.nbacm.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor

public class CoComment extends BaseTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coCommentId;
    private String nickname;
    private String comment;
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment parentComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;

    public CoComment(CoCommentRequestDto commentRequestDto, Comment comment, User user) {
        this.coCommentId = commentRequestDto.getId();
        this.nickname = user.getNickname();
        this.comment = commentRequestDto.getComment();
        this.parentComment = comment;
        this.email = user.getEmail();

    }

    public CoComment(Comment comment) {
        this.coCommentId = comment.getCommentId();
        this.nickname = comment.getNickname();
        this.comment = comment.getComment();
    }

    public void update(CoCommentRequestDto coCommentRequestDto, CoComment coComment, User user) {
        this.nickname = user.getNickname();
        this.comment = coCommentRequestDto.getComment();
        this.user = user;
    }
}
