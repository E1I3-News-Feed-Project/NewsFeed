package com.nbacm.newsfeed.domain.comment.dto.request;

import lombok.Getter;

@Getter
public class CoCommentRequestDto {
    private Long id;
    private String comment;
    private String nickname;
}
