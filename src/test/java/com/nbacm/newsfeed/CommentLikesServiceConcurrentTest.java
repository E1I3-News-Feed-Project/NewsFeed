package com.nbacm.newsfeed;

import com.nbacm.newsfeed.domain.likes.service.CommentLikesService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.likes.dto.response.CommentLikesResponse;
import com.nbacm.newsfeed.domain.likes.repositroy.CommentLikesRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379"
})
public class CommentLikesServiceConcurrentTest {
    @Autowired
    private CommentLikesService commentLikesService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentLikesRepository commentLikesRepository;

    private Comment testComment;
    private List<User> testUsers;


    @BeforeEach
    void setUp() {
        commentLikesRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();

        // testComment 생성 시 필요한 필드를 설정
        testComment = new Comment();
        testComment.setComment("Test Comment");
        testComment.setCommentLikesCount(0);
        testComment.setNickname("TestUser");
        testComment.setReplyCount(0);
        testComment.setCreatedAt(LocalDateTime.now());
        testComment.setUpdatedAt(LocalDateTime.now());
        testComment = commentRepository.save(testComment);

        testUsers = new ArrayList<>();
        for (int i = 0; i < 110; i++) {
            User user = new User();
            user.setEmail("user" + i + "@test.com");
            user.setNickname("TestUser"+i);
            testComment.setNickname("TestUser"+i);
            user.setPassword("Knh7841526@"+i);
            testUsers.add(userRepository.save(user));
        }
    }

    @Test
    void testConcurrentLikeComment() throws InterruptedException {
        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            service.execute(() -> {
                try {
                    CommentLikesResponse response = commentLikesService.likeComment(testComment.getCommentId(), testUsers.get(index).getEmail());
                    System.out.println("id:"+testComment.getCommentId());
                    if (response != null) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println("예외발생"+e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        service.shutdown();

        Comment updatedComment = commentRepository.findById(testComment.getCommentId()).orElseThrow();
        long actualLikesCount = commentLikesRepository.countByComment(updatedComment);

        assertEquals(successCount.get(), updatedComment.getCommentLikesCount());
        assertEquals(successCount.get(), actualLikesCount);
        assertEquals(100, successCount.get(), "모든 좋아요 시도가 성공해야 합니다.");
    }
}
