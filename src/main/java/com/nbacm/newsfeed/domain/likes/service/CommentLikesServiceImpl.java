package com.nbacm.newsfeed.domain.likes.service;

import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.exception.BadRequestException;
import com.nbacm.newsfeed.domain.likes.dto.response.CommentLikesResponse;
import com.nbacm.newsfeed.domain.likes.entity.CommentLikes;
import com.nbacm.newsfeed.domain.likes.repository.CommentLikesRepository;
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
public class CommentLikesServiceImpl implements CommentLikesService {

    private final CommentLikesRepository commentLikesRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String COMMENT_LIKES_COUNT_KEY = "comment:likes:count:";
    private static final String USER_LIKED_COMMENT_KEY = "user:liked:comment:";

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public CommentLikesResponse likeComment(Long commentId, String email) {
        RLock lock = redissonClient.getLock("commentLikeLock:" + commentId);
        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                try {
                    User user = userRepository.findByEmailOrElseThrow(email);
                    Comment comment = commentRepository.findById(commentId).orElseThrow(RuntimeException::new);

                    String userLikedKey = USER_LIKED_COMMENT_KEY + user.getUserId() + ":" + commentId;
                    String hasLikedStr = redisTemplate.opsForValue().get(userLikedKey);
                    Boolean hasLiked = hasLikedStr != null ? Boolean.parseBoolean(hasLikedStr) : null;

                    if (hasLiked == null) {
                        hasLiked = commentLikesRepository.existsByUserAndComment(user, comment);
                        redisTemplate.opsForValue().set(userLikedKey, String.valueOf(hasLiked), 1, TimeUnit.HOURS);
                    }

                    if (Boolean.TRUE.equals(hasLiked)) {
                        throw new BadRequestException("이미 좋아요를 누른 댓글입니다.");
                    }

                    CommentLikes commentLikes = CommentLikes.builder()
                            .user(user)
                            .comment(comment)
                            .build();
                    commentLikesRepository.save(commentLikes);

                    String likesCountKey = COMMENT_LIKES_COUNT_KEY + commentId;
                    Long newLikesCount = redisTemplate.opsForValue().increment(likesCountKey);
                    redisTemplate.opsForValue().set(userLikedKey, "true", 1, TimeUnit.HOURS);

                    comment.increaseLikesCount();
                    commentRepository.save(comment);

                    comment.setCommentLikesCount(newLikesCount != null ? newLikesCount.intValue() : comment.getCommentLikesCount());
                    return CommentLikesResponse.from(comment);
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            } else {
                throw new BadRequestException("좋아요 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            }
        } catch (InterruptedException e) {
            throw new BadRequestException("좋아요 처리 중 오류가 발생했습니다.");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public CommentLikesResponse unlikeComment(Long commentId, String email) {
        RLock lock = redissonClient.getLock("commentUnlikeLock:" + commentId);
        try {
            if (lock.tryLock(20, 10, TimeUnit.SECONDS)) {
                try {
                    log.info("락 획득 성공: commentId={}, email={}", commentId, email);

                    User user = userRepository.findByEmailOrElseThrow(email);
                    Comment comment = commentRepository.findById(commentId).orElseThrow(RuntimeException::new);

                    String userLikedKey = USER_LIKED_COMMENT_KEY + user.getUserId() + ":" + commentId;
                    String hasLikedStr = redisTemplate.opsForValue().get(userLikedKey);
                    Boolean hasLiked = hasLikedStr != null ? Boolean.parseBoolean(hasLikedStr) : null;

                    if (hasLiked == null) {
                        hasLiked = commentLikesRepository.existsByUserAndComment(user, comment);
                    }

                    if (Boolean.FALSE.equals(hasLiked)) {
                        throw new BadRequestException("이미 좋아요가 취소되었습니다.");
                    }

                    commentLikesRepository.deleteByUserAndComment(user, comment);

                    String likesCountKey = COMMENT_LIKES_COUNT_KEY + commentId;
                    Long newLikesCount = redisTemplate.opsForValue().decrement(likesCountKey);
                    redisTemplate.delete(userLikedKey);

                    if (comment.getCommentLikesCount() > 0) {
                        comment.decreaseLikesCount();
                        commentRepository.save(comment);
                    }

                    comment.setCommentLikesCount(newLikesCount != null ? newLikesCount.intValue() : comment.getCommentLikesCount());
                    return CommentLikesResponse.from(comment);
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        log.info("락 해제 성공: commentId={}, email={}", commentId, email);
                    }
                }
            } else {
                log.warn("락 획득 실패: commentId={}, email={}", commentId, email);
                throw new BadRequestException("좋아요 취소 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            }
        } catch (InterruptedException e) {
            log.error("좋아요 취소 처리 중 오류가 발생했습니다.");
            throw new BadRequestException("좋아요 취소 처리 중 오류가 발생했습니다.");
        }
    }
}