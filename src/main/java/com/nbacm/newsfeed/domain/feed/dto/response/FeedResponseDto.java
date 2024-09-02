package com.nbacm.newsfeed.domain.feed.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedResponseDto {
    private Long feedId;
    private String content;
    private List<String> images; // 이미지 이름 리스트
    private String authorEmail;
    private int likesCount;
}