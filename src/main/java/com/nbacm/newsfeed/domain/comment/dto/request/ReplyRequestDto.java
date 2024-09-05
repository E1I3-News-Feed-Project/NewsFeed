package com.nbacm.newsfeed.domain.comment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReplyRequestDto {
    private Long id;
    private String comment;
    private String nickname;

    public ReplyRequestDto(String replyContent, String nickname) {
        this.comment = replyContent;
        this.nickname = nickname;
    }
}
