package com.nbacm.newsfeed;

import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.likes.dto.response.CommentLikesResponse;
import com.nbacm.newsfeed.domain.likes.entity.CommentLikes;
import com.nbacm.newsfeed.domain.likes.repositroy.CommentLikesRepository;
import com.nbacm.newsfeed.domain.likes.service.CommentLikesService;
import com.nbacm.newsfeed.domain.likes.service.CommentLikesServiceImpl;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentLikesServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CommentLikesServiceTest.class);

    @Mock
    private CommentLikesRepository commentLikesRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    private CommentLikesServiceImpl commentLikesService;

    private List<User> users;
    private List<Comment> comments;
    private Random random;


    @BeforeEach
    void setUp() {
        users = new ArrayList<>();
        comments = new ArrayList<>();
        random = new Random();

        // 10명의 사용자 생성
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUserId((long) i);
            user.setEmail("user" + i + "@example.com");
            users.add(user);
        }

        // 20개의 댓글 생성
        for (int i = 0; i < 20; i++) {
            Comment comment = new Comment();
            comment.setCommentId((long) i);
            comments.add(comment);
        }

        // UserRepository mock 설정
        when(userRepository.finByEmailOrElseThrow(anyString())).thenAnswer(invocation -> {
            String email = invocation.getArgument(0);
            return users.stream()
                    .filter(u -> u.getEmail().equals(email))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"));
        });

        // CommentRepository mock 설정
        when(commentRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return comments.stream()
                    .filter(c -> c.getCommentId().equals(id))
                    .findFirst();
        });
    }

    @Test
    void likeComment_MultiUserConcurrentAccess() throws InterruptedException {
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        // CommentLikesRepository mock 설정
        Map<String, Boolean> likeStatus = new HashMap<>();
        when(commentLikesRepository.existsByUserAndComment(any(User.class), any(Comment.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    Comment comment = invocation.getArgument(1);
                    String key = user.getEmail() + "-" + comment.getCommentId();
                    return likeStatus.getOrDefault(key, false);
                });

        when(commentLikesRepository.save(any(CommentLikes.class))).thenAnswer(invocation -> {
            CommentLikes like = invocation.getArgument(0);
            String key = like.getUser().getUserId() + "-" + like.getComment().getCommentId();
            likeStatus.put(key, true);
            logger.info("Thread {}: Saved like for user {} on comment {}",
                    Thread.currentThread().getId(), like.getUser().getUserId(), like.getComment().getComment());
            return like;
        });

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    latch.countDown();
                    latch.await(); // 모든 스레드가 준비될 때까지 대기
                    User randomUser = users.get(random.nextInt(users.size()));
                    Comment randomComment = comments.get(random.nextInt(comments.size()));
                    logger.info("Thread {}: User {} trying to like comment {}",
                            Thread.currentThread().getId(), randomUser.getUserId(), randomComment.getCommentId());
                    CommentLikesResponse response = commentLikesService.likeComment(randomComment.getCommentId(), randomUser.getEmail());
                    if (response != null) {
                        logger.info("Thread {}: User {} successfully liked comment {}",
                                Thread.currentThread().getId(), randomUser.getUserId(), randomComment.getComment());
                        successCount.incrementAndGet();
                    } else {
                        logger.info("Thread {}: User {} failed to like comment {}",
                                Thread.currentThread().getId(), randomUser.getUserId(), randomComment.getComment());
                    }
                } catch (Exception e) {
                    logger.error("Thread {}: Error while liking comment", Thread.currentThread().getId(), e);
                }
            });
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.sleep(100);
        }

        logger.info("All threads finished. Verifying results...");
        logger.info("Total success count: {}", successCount.get());

        // 검증
        verify(commentLikesRepository, times(successCount.get())).save(any(CommentLikes.class));

        logger.info("Test completed. All assertions passed.");
    }
}
