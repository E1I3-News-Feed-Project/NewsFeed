package com.nbacm.newsfeed.domain.likes.service;

import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.feed.repository.FeedRepository;
import com.nbacm.newsfeed.domain.likes.dto.response.FeedLikesResponse;
import com.nbacm.newsfeed.domain.likes.entity.FeedLikes;
import com.nbacm.newsfeed.domain.likes.repositroy.FeedLikesRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FeedLikesServiceImpl implements FeedLikesService {

    private final FeedLikesRepository feedLikesRepository;
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public FeedLikesResponse likeFeed(Long feedId, String email) {
        User user = userRepository.finByEmailOrElseThrow(email);
        Feed feed = feedRepository.findById(feedId).orElseThrow(RuntimeException::new);

        if (feedLikesRepository.existsByUserAndFeed(user, feed)) {
            throw new RuntimeException("이미 좋아요를 누른 피드입니다.");
        }

        FeedLikes feedLikes = FeedLikes.builder()
                .feed(feed)
                .user(user)
                .build();

        feedLikesRepository.save(feedLikes);

        feed.increaseLikesCount();

        return FeedLikesResponse.from(feed);
    }

    @Transactional
    @Override
    public FeedLikesResponse unlikeFeed(Long feedId, String email) {
        User user = userRepository.finByEmailOrElseThrow(email);
        Feed feed = feedRepository.findById(feedId).orElseThrow(RuntimeException::new);

        if (!feedLikesRepository.existsByUserAndFeed(user, feed)) {
            throw new RuntimeException("관련된 정보가 없습니다.");
        }

        feedLikesRepository.deleteByUserAndFeed(user, feed);

        feed.decreaseLikesCount();

        return FeedLikesResponse.from(feed);
    }
}
