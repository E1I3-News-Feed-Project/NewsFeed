package com.nbacm.newsfeed.domain.comment.entity;

import com.nbacm.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.time.entity.BaseTime;
import com.nbacm.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    private String comment;
    private Long feedId;
    private String email;
    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyComment> replyComments = new ArrayList<>();

    private int commentLikesCount;
    private int replyCount;

    public Comment(CommentRequestDto commentRequestDto, Feed feed, User user) {
        this.commentId = commentRequestDto.getId();
        this.feed = feed;
        this.comment = commentRequestDto.getComment();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.user = user;
    }


    public void updateComment(CommentRequestDto commentRequestDto, Feed feed, Comment comment, User user) {
        this.feedId = feed.getFeedId();
        this.commentId = comment.getCommentId();
        this.comment = commentRequestDto.getComment();
        this.nickname = user.getNickname();
        this.user = user;
        this.email = user.getEmail();
    }

    public void increaseLikesCount() {
        this.commentLikesCount++;
    }

    public void decreaseLikesCount() {
        this.commentLikesCount++;
    }

    public void incrementReplyCount() {
        this.replyCount++;
    }

    public void decrementReplyCount() {
        if (this.replyCount > 0) {
            this.replyCount--;
        }
    }
}
