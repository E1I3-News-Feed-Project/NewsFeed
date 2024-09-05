package com.nbacm.newsfeed.domain.likes.service;

import com.nbacm.newsfeed.domain.exception.BadRequestException;
import com.nbacm.newsfeed.domain.exception.NotFoundException;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.feed.repository.FeedRepository;
import com.nbacm.newsfeed.domain.likes.dto.response.FeedLikesResponse;
import com.nbacm.newsfeed.domain.likes.entity.FeedLikes;
import com.nbacm.newsfeed.domain.likes.repository.FeedLikesRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class FeedLikesServiceImpl implements FeedLikesService {

    private final FeedLikesRepository feedLikesRepository;
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String FEED_LIKES_COUNT_KEY = "feed:likes:count:";
    private static final String USER_LIKED_FEED_KEY = "user:liked:feed:";

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public FeedLikesResponse likeFeed(Long feedId, String email) {
        RLock lock = redissonClient.getLock("feedLikeLock:" + feedId);
        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                try {
                    log.info("락 획득 성공: feedId={}, email={}", feedId, email);
                    User user = userRepository.findByEmailOrElseThrow(email);
                    Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new NotFoundException("피드를 찾을 수 없습니다."));

                    String userLikedKey = USER_LIKED_FEED_KEY + user.getUserId() + ":" + feedId;
                    String hasLikedStr = redisTemplate.opsForValue().get(userLikedKey);
                    Boolean hasLiked = hasLikedStr != null ? Boolean.parseBoolean(hasLikedStr) : null;

                    if (hasLiked == null) {
                        hasLiked = feedLikesRepository.existsByUserAndFeed(user, feed);
                        redisTemplate.opsForValue().set(userLikedKey, String.valueOf(hasLiked), 1, TimeUnit.HOURS);
                    }

                    if (Boolean.TRUE.equals(hasLiked)) {
                        throw new BadRequestException("이미 좋아요를 누른 피드입니다.");
                    }

                    FeedLikes feedLikes = FeedLikes.builder()
                            .feed(feed)
                            .user(user)
                            .build();
                    feedLikesRepository.save(feedLikes);

                    String likesCountKey = FEED_LIKES_COUNT_KEY + feedId;
                    Long newLikesCount = redisTemplate.opsForValue().increment(likesCountKey);
                    redisTemplate.opsForValue().set(userLikedKey, "true", 1, TimeUnit.HOURS);

                    feed.increaseLikesCount();
                    feedRepository.save(feed);

                    feed.setLikesCount(newLikesCount != null ? newLikesCount.intValue() : feed.getLikesCount());
                    return FeedLikesResponse.from(feed);
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        log.info("락 해제 성공: feedId={}, email={}", feedId, email);
                    }
                }
            } else {
                throw new BadRequestException("좋아요 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("좋아요 처리 중 오류가 발생했습니다.");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public FeedLikesResponse unlikeFeed(Long feedId, String email) {
        RLock lock = redissonClient.getLock("feedUnlikeLock:" + feedId);
        try {
            if (lock.tryLock(20, 10, TimeUnit.SECONDS)) {
                try {
                    log.info("락 획득 성공: feedId={}, email={}", feedId, email);

                    User user = userRepository.findByEmailOrElseThrow(email);
                    Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new NotFoundException("피드를 찾을 수 없습니다."));

                    String userLikedKey = USER_LIKED_FEED_KEY + user.getUserId() + ":" + feedId;
                    String hasLikedStr = redisTemplate.opsForValue().get(userLikedKey);
                    Boolean hasLiked = hasLikedStr != null ? Boolean.parseBoolean(hasLikedStr) : null;

                    if (hasLiked == null) {
                        hasLiked = feedLikesRepository.existsByUserAndFeed(user, feed);
                    }

                    if (Boolean.FALSE.equals(hasLiked)) {
                        throw new BadRequestException("이미 좋아요가 취소되었습니다.");
                    }

                    feedLikesRepository.deleteByUserAndFeed(user, feed);

                    String likesCountKey = FEED_LIKES_COUNT_KEY + feedId;
                    Long newLikesCount = redisTemplate.opsForValue().decrement(likesCountKey);
                    redisTemplate.delete(userLikedKey);

                    if (feed.getLikesCount() > 0) {
                        feed.decreaseLikesCount();
                        feedRepository.save(feed);
                    }

                    if (newLikesCount != null && newLikesCount <= 0) {
                        redisTemplate.delete(likesCountKey);
                    }

                    feed.setLikesCount(newLikesCount != null ? Math.max(newLikesCount.intValue(), 0) : 0);
                    return FeedLikesResponse.from(feed);
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        log.info("락 해제 성공: feedId={}, email={}", feedId, email);
                    }
                }
            } else {
                log.warn("락 획득 실패: feedId={}, email={}", feedId, email);
                throw new BadRequestException("좋아요 취소 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            }
        } catch (InterruptedException e) {
            log.error("좋아요 취소 처리 중 오류가 발생했습니다.");
            throw new BadRequestException("좋아요 취소 처리 중 오류가 발생했습니다.");
        }
    }
}