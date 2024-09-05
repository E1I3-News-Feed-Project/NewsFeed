package com.nbacm.newsfeed.domain.likes.entity;

import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.comment.entity.Comment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class CommentLikes {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentLikeId;

    @ManyToOne
    private Comment comment;

    @ManyToOne
    private User user;

    @Builder
    private CommentLikes(Comment comment, User user) {
        this.comment = comment;
        this.user = user;
    }
}
