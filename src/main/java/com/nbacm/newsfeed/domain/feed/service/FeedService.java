package com.nbacm.newsfeed.domain.feed.service;

import com.nbacm.newsfeed.domain.feed.dto.request.FeedRequestDto;
import com.nbacm.newsfeed.domain.feed.dto.response.FeedResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FeedService {
    void createFeed(FeedRequestDto requestDto) throws IOException;

    Optional<FeedResponseDto> getFeedById(Long feedId, String email);

    boolean deleteFeed(Long feedId, String email);

    List<FeedResponseDto> getAllFeeds(String email, Pageable pageable);

    FeedResponseDto updateFeed(Long feedId, FeedRequestDto feedRequestDto, List<MultipartFile> images, String email) throws IOException;

    List<FeedResponseDto> getFriendsFeeds(String request);
}