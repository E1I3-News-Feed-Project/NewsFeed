package com.nbacm.newsfeed.domain.comment.entity;

import com.nbacm.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.time.entity.BaseTime;
import com.nbacm.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "comment")

public class Comment extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyComment> replies = new ArrayList<>();

    private String comment;
    private int commentLikesCount;
    private int replyCount;

    public Comment(CommentRequestDto commentRequestDto, Feed feed, User user) {
        this.commentId = commentRequestDto.getId();
        this.feed = feed;
        this.comment = commentRequestDto.getComment();
        this.user = user;
    }


    public void updateComment(CommentRequestDto commentRequestDto, Feed feed, Comment comment, User user) {
        this.feed = feed;
        this.commentId = comment.getCommentId();
        this.comment = commentRequestDto.getComment();
        this.user = user;
    }

    public void increaseLikesCount() {
        this.commentLikesCount++;
    }

    public void decreaseLikesCount() {
        this.commentLikesCount--;
    }

    public void incrementReplyCount() {
        this.replyCount++;
    }

    public void decrementReplyCount() {
        if (this.replyCount > 0) {
            this.replyCount--;
        }
    }

    public void setNickname(String testUser) {
    }
}
