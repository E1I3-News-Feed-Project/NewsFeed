package com.nbacm.newsfeed;

import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.feed.repository.FeedRepository;
import com.nbacm.newsfeed.domain.likes.dto.response.FeedLikesResponse;
import com.nbacm.newsfeed.domain.likes.repository.FeedLikesRepository;
import com.nbacm.newsfeed.domain.likes.service.FeedLikesService;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


@SpringBootTest
@TestPropertySource(properties = {
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379"
})
public class FeedLikesConcurrentTest {

    @Autowired
    private FeedLikesService feedLikesService;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedLikesRepository feedLikesRepository;

    private Feed testFeed;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        feedLikesRepository.deleteAll();
        feedRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트 피드 생성
        User feedOwner = new User();
        feedOwner.setEmail("feedowner@test.com");
        feedOwner.setNickname("FeedOwner");
        feedOwner.setPassword("TestPassword123!");
        userRepository.save(feedOwner);

        testFeed = new Feed("Test Feed Content", feedOwner, 0);
        testFeed = feedRepository.save(testFeed);

        // 테스트 사용자 생성
        testUsers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setEmail("user" + i + "@test.com");
            user.setNickname("User" + i);
            user.setPassword("TestPassword" + i + "!");
            testUsers.add(userRepository.save(user));
        }
    }

    @Test
    void testConcurrentLikeFeed() throws InterruptedException {
        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            service.execute(() -> {
                try {
                    FeedLikesResponse response = feedLikesService.likeFeed(testFeed.getFeedId(), testUsers.get(index).getEmail());
                    if (response != null) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println("Exception occurred: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        service.shutdown();

    }
}
