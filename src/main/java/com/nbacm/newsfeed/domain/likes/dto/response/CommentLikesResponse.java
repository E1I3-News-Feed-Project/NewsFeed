package com.nbacm.newsfeed.domain.likes.dto.response;

import com.nbacm.newsfeed.domain.comment.entity.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CommentLikesResponse {
    private Long commentId;
    private Integer commentLikesCount;

    public static CommentLikesResponse from(Comment comment) {
        return new CommentLikesResponse(comment.getCommentId(), comment.getCommentLikesCount());
    }
}
