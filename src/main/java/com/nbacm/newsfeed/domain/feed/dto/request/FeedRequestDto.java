package com.nbacm.newsfeed.domain.feed.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FeedRequestDto {
    private String content;
    private List<MultipartFile> images;
    private String email; // 추가된 필드

}