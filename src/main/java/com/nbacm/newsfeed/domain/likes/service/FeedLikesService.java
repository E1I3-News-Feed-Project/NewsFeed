package com.nbacm.newsfeed.domain.likes.service;

import com.nbacm.newsfeed.domain.likes.dto.response.FeedLikesResponse;

public interface FeedLikesService {
    FeedLikesResponse likeFeed(Long feedId, String email);
    FeedLikesResponse unlikeFeed(Long feedId, String email);
}
