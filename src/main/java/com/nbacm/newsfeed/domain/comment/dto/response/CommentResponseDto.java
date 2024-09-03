package com.nbacm.newsfeed.domain.comment.dto.response;

import com.nbacm.newsfeed.domain.comment.entity.Comment;
import lombok.Getter;

@Getter
public class CommentResponseDto {
    private Long id;
    private String comment;
    private String nickname;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getCommentId();
        this.comment = comment.getComment();
        this.nickname = String.valueOf(comment.getUser().getNickname());

    }
}
