package com.nbacm.newsfeed.domain.comment.dto.response;

import com.nbacm.newsfeed.domain.comment.entity.CoComment;
import com.nbacm.newsfeed.domain.comment.entity.Comment;
import lombok.Getter;

@Getter
public class CoCommentResponseDto {
    private Long id;
    private String comment;
    private String nickname;

    public CoCommentResponseDto(CoComment coComment) {
        this.id = coComment.getCoCommentId();
        this.comment = coComment.getComment();
        this.nickname = coComment.getNickname();
//        this.nickname = String.valueOf(coComment.getNickname());

    }
}
