package com.nbacm.newsfeed.domain.comment.dto.response;

import com.nbacm.newsfeed.domain.comment.entity.ReplyComment;
import lombok.Getter;

@Getter
public class ReplyResponseDto {
    private Long id;
    private String comment;
    private String nickname;

    public ReplyResponseDto(ReplyComment replyComment) {
        this.id = replyComment.getCoCommentId();
        this.comment = replyComment.getComment();
        this.nickname = replyComment.getUser().getNickname();
    }
}
