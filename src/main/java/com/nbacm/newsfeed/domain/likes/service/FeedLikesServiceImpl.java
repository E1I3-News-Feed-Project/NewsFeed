package com.nbacm.newsfeed.domain.likes.service;

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



    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public FeedLikesResponse likeFeed(Long feedId, String email) {
        RLock lock = redissonClient.getLock("feedLikeLock:" + feedId);
        try {
            // 락을 최대 10초 동안 시도하며, 락을 잡으면 5초 동안 유지
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                try {
                    // 사용자와 피드를 조회
                    User user = userRepository.findByEmailOrElseThrow(email);
                    Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new RuntimeException("피드를 찾을 수 없습니다."));

                    // 이미 좋아요를 누른 피드인지 확인
                    if (feedLikesRepository.existsByUserAndFeed(user, feed)) {
                        throw new RuntimeException("이미 좋아요를 누른 피드입니다.");
                    }

                    // 좋아요 엔티티를 생성 및 저장
                    FeedLikes feedLikes = FeedLikes.builder()
                            .feed(feed)
                            .user(user)
                            .build();
                    feedLikesRepository.save(feedLikes);

                    // 피드의 좋아요 수를 증가
                    feed.increaseLikesCount();
                    feedRepository.save(feed); // 좋아요 수 반영을 위해 저장

                    // 응답 반환
                    return FeedLikesResponse.from(feed);
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock(); // 락 해제
                    }
                }
            } else {
                throw new RuntimeException("좋아요 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("좋아요 처리 중 오류가 발생했습니다.", e);
        }
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public FeedLikesResponse unlikeFeed(Long feedId, String email) {
        RLock lock = redissonClient.getLock("feedUnlikeLock:" + feedId);
        try {
            // 락을 최대 20초 동안 시도하며, 락을 잡으면 10초 동안 유지
            if (lock.tryLock(20, 10, TimeUnit.SECONDS)) {
                try {
                    // 락 획득 확인 로그
                    log.info("락 획득 성공: feedId={}, email={}", feedId, email);

                    // 사용자와 피드를 조회
                    User user = userRepository.findByEmailOrElseThrow(email);
                    Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new RuntimeException("피드를 찾을 수 없습니다."));

                    // 좋아요 취소할 정보가 있는지 확인
                    if (!feedLikesRepository.existsByUserAndFeed(user, feed)) {
                        throw new RuntimeException("이미 좋아요가 취소되었습니다.");
                    }

                    // 좋아요 정보 삭제
                    feedLikesRepository.deleteByUserAndFeed(user, feed);

                    // 좋아요 수 감소, 0 이상이어야 감소 가능
                    if (feed.getLikesCount() > 0) {
                        feed.decreaseLikesCount();
                        feedRepository.save(feed);  // 좋아요 수 반영
                    }

                    // 응답 반환
                    return FeedLikesResponse.from(feed);
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock(); // 락 해제
                        log.info("락 해제 성공: feedId={}, email={}", feedId, email);
                    }
                }
            } else {
                // 락 획득 실패 로그
                log.warn("락 획득 실패: feedId={}, email={}", feedId, email);
                throw new RuntimeException("좋아요 취소 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            }
        } catch (InterruptedException e) {
            log.error("좋아요 취소 처리 중 오류가 발생했습니다.", e);
            throw new RuntimeException("좋아요 취소 처리 중 오류가 발생했습니다.", e);
        }
    }
}
