package com.nbacm.newsfeed.domain.feed.dto.response;

import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.feed.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

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

    // 정적 메서드를 추가
    public static FeedResponseDto from(Feed feed) {
        List<String> imageNames = feed.getImages().stream()
                .map(Image::getImageName)
                .collect(Collectors.toList());

        return new FeedResponseDto(
                feed.getFeedId(),
                feed.getContent(),
                imageNames,
                feed.getAuthor().getEmail(),
                feed.getLikesCount()
        );
    }
}