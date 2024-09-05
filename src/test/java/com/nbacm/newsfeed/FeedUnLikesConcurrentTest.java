package com.nbacm.newsfeed;

import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.feed.repository.FeedRepository;
import com.nbacm.newsfeed.domain.likes.dto.response.FeedLikesResponse;
import com.nbacm.newsfeed.domain.likes.entity.FeedLikes;
import com.nbacm.newsfeed.domain.likes.repository.FeedLikesRepository;
import com.nbacm.newsfeed.domain.likes.service.FeedLikesService;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.data.redis.host=13.124.17.212",
        "spring.data.redis.port=6379"
})
public class FeedUnLikesConcurrentTest {

    @Autowired
    private FeedLikesService feedLikesService;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedLikesRepository feedLikesRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private Feed testFeed;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        feedLikesRepository.deleteAll();
        feedRepository.deleteAll();
        userRepository.deleteAll();

        // Redis의 모든 키 삭제
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        // 테스트 피드 생성
        User feedOwner = new User();
        feedOwner.setEmail("feedowner@test.com");
        feedOwner.setNickname("FeedOwner");
        feedOwner.setPassword("TestPassword123!");
        userRepository.save(feedOwner);

        testFeed = new Feed("Test Feed Content", feedOwner, 100);  // 초기 좋아요 수를 100으로 설정
        testFeed = feedRepository.save(testFeed);

        // 테스트 사용자 생성 및 좋아요 추가
        testUsers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setEmail("user" + i + "@test.com");
            user.setNickname("User" + i);
            user.setPassword("TestPassword" + i + "!");
            user = userRepository.save(user);
            testUsers.add(user);

            feedLikesService.likeFeed(testFeed.getFeedId(), user.getEmail());
        }

        printRedisKeys("초기 상태의 Redis 키:");
    }

    @Test
    void testConcurrentUnlikeFeed() throws InterruptedException {
        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            service.execute(() -> {
                try {
                    FeedLikesResponse response = feedLikesService.unlikeFeed(testFeed.getFeedId(), testUsers.get(index).getEmail());
                    if (response != null) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                    exceptionCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        service.shutdown();

        // 결과 확인
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("예외 발생 횟수: " + exceptionCount.get());

        printRedisKeys("테스트 후 Redis 키:");

        // 좋아요 수 확인
        Feed updatedFeed = feedRepository.findById(testFeed.getFeedId()).orElseThrow();
        System.out.println("최종 좋아요 수: " + updatedFeed.getLikesCount());

        // Assertions
        assertEquals(numberOfThreads, successCount.get() + exceptionCount.get(), "모든 요청이 처리되어야 합니다.");
        assertEquals(0, updatedFeed.getLikesCount(), "모든 좋아요가 취소되어 좋아요 수가 0이 되어야 합니다.");

        cleanupRedisKeys();
    }

    private void printRedisKeys(String message) {
        System.out.println(message);
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            System.out.println(key + ": " + redisTemplate.opsForValue().get(key));
        }
    }

    private void cleanupRedisKeys() {
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            redisTemplate.delete(key);
        }
        System.out.println("모든 Redis 키가 삭제되었습니다.");
    }
}