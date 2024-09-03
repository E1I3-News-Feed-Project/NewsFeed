package com.nbacm.newsfeed.domain.comment.entity;

import com.nbacm.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.time.entity.BaseTime;
import com.nbacm.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Comment extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
//    private Long feedId;
    private String nickname;
    private String comment;
    private Long feedId;
    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private int  commentLikesCount;

    public Comment(CommentRequestDto commentRequestDto, Feed feed, User user) {
//        this.feedId = feed.getFeedId();
        this.commentId = commentRequestDto.getId();
        this.feed = feed;
        this.comment = commentRequestDto.getComment();
        this.nickname = user.getNickname();
        this.user = user;
    }


    public void updateComment(CommentRequestDto commentRequestDto, Feed feed, Comment comment, User user) {
        this.feedId = feed.getFeedId();
        this.commentId = comment.getCommentId();
        this.comment = commentRequestDto.getComment();
        this.nickname = commentRequestDto.getNickname();
        this.user = user;
    }

    public void increaseLikesCount() {
        this.commentLikesCount++;
    }

    public void decreaseLikesCount() {
        this.commentLikesCount++;
    }

}
