package com.nbacm.newsfeed;


import com.nbacm.newsfeed.domain.likes.service.CommentLikesService;
import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.likes.dto.response.CommentLikesResponse;
import com.nbacm.newsfeed.domain.likes.repository.CommentLikesRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest

@TestPropertySource(properties = {
        "spring.data.redis.host=13.124.17.212",
        "spring.data.redis.port=6379"
})
public class CommentLikesServiceConcurrentUnlikeTest {

    @Autowired
    private CommentLikesService commentLikesService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentLikesRepository commentLikesRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private Comment testComment;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        commentLikesRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();
        // testComment 생성
        testComment = new Comment();
        testComment.setComment("Test Comment");
        testComment.setCommentLikesCount(0);
        testComment.setNickname("TestUser");
        testComment.setReplyCount(0);
        testComment.setCreatedAt(LocalDateTime.now());
        testComment.setUpdatedAt(LocalDateTime.now());
        testComment = commentRepository.save(testComment);
        // testUsers 생성
        testUsers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setEmail("user" + i + "@test.com");
            user.setNickname("User" + i);
            user.setPassword("Knh7841526@" + i);
            testUsers.add(userRepository.save(user));

            // 각 유저가 테스트 댓글에 좋아요를 먼저 추가
            commentLikesService.likeComment(testComment.getCommentId(), user.getEmail());
        }
    }

    @Test
    void testConcurrentUnlikeComment() throws InterruptedException {
        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            service.execute(() -> {
                try {
                    // 각 스레드가 좋아요 취소를 시도
                    CommentLikesResponse response = commentLikesService.unlikeComment(testComment.getCommentId(), testUsers.get(index).getEmail());
                    if (response != null) {
                        failCount.decrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        service.shutdown();
        printRedisKeys("테스트 후 Redis 키:");

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
