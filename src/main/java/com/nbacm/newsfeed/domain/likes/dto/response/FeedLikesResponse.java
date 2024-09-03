package com.nbacm.newsfeed.domain.likes.dto.response;


import com.nbacm.newsfeed.domain.feed.entity.Feed;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FeedLikesResponse {
    private Long feedId;
    private Integer likesCount;

    public static FeedLikesResponse from(Feed feed) {
        return new FeedLikesResponse(feed.getFeedId(), feed.getLikesCount());
    }
}
