package com.nbacm.newsfeed.domain.comment.entity;

import com.nbacm.newsfeed.domain.comment.dto.request.ReplyRequestDto;
import com.nbacm.newsfeed.domain.time.entity.BaseTime;
import com.nbacm.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "reply_comment")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)

public class ReplyComment extends BaseTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coCommentId;

    private String nickname;
    private String comment;
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public ReplyComment(ReplyRequestDto commentRequestDto, Comment comment, User user) {
        this.coCommentId = commentRequestDto.getId();
        this.nickname = user.getNickname();
        this.comment = commentRequestDto.getComment();
        this.parentComment = comment;
        this.email = user.getEmail();

    }

    public ReplyComment(Comment comment) {
        this.coCommentId = comment.getCommentId();
        this.comment = comment.getComment();
    }

    public void update(ReplyRequestDto replyRequestDto, ReplyComment replyComment, User user) {
        this.nickname = user.getNickname();
        this.comment = replyRequestDto.getComment();
        this.user = user;
    }
}
