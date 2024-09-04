package com.nbacm.newsfeed.domain.likes.service;

import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.likes.dto.response.CommentLikesResponse;
import com.nbacm.newsfeed.domain.likes.entity.CommentLikes;
import com.nbacm.newsfeed.domain.likes.repository.CommentLikesRepository;
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
public class CommentLikesServiceImpl implements CommentLikesService {

    private final CommentLikesRepository commentLikesRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final RedissonClient redissonClient;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public CommentLikesResponse likeComment(Long commentId, String email) {
        RLock lock = redissonClient.getLock("commentLikeLock:" + commentId);
        try {
            // 락을 최대 10초 동안 시도하며, 락을 잡으면 5초 동안 유지
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                try {
                    // 사용자와 댓글을 조회
                    User user = userRepository.findByEmailOrElseThrow(email);
                    Comment comment = commentRepository.findById(commentId).orElseThrow(RuntimeException::new);
                    // 이미 좋아요를 누른 댓글인지 확인
                    if (commentLikesRepository.existsByUserAndComment(user, comment)) {
                        throw new RuntimeException("이미 좋아요를 누른 댓글입니다.");
                    }
                    // 좋아요 엔티티를 생성 및 저장
                    CommentLikes commentLikes = CommentLikes.builder()
                            .user(user)
                            .comment(comment)
                            .build();
                    commentLikesRepository.save(commentLikes);
                    // 댓글의 좋아요 수를 증가
                    comment.increaseLikesCount();
                    commentRepository.save(comment); // 좋아요 수 반영을 위해 저장
                    // 응답 반환
                    return CommentLikesResponse.from(comment);
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock(); // 락 해제
                    }
                }
            } else {
                throw new RuntimeException("좋아요 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("좋아요 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public CommentLikesResponse unlikeComment(Long commentId, String email) {
        // Redisson을 이용해 commentId 기반으로 분산 락을 걸기
        RLock lock = redissonClient.getLock("commentUnlikeLock:" + commentId);
        try {
            // 락을 최대 15초 동안 시도하며, 락을 잡으면 5초 동안 유지
            if (lock.tryLock(20, 10, TimeUnit.SECONDS)) {
                try {
                    // 락 획득 확인 로그
                    log.info("락 획득 성공: commentId={}, email={}", commentId, email);

                    // 사용자와 댓글을 조회
                    User user = userRepository.findByEmailOrElseThrow(email);
                    Comment comment = commentRepository.findById(commentId).orElseThrow(RuntimeException::new);

                    // 좋아요 취소할 정보가 있는지 확인
                    if (!commentLikesRepository.existsByUserAndComment(user, comment)) {
                        throw new RuntimeException("이미 좋아요가 취소되었습니다.");
                    }

                    // 좋아요 정보 삭제
                    commentLikesRepository.deleteByUserAndComment(user, comment);

                    // 좋아요 수 감소, 0 이상이어야 감소 가능
                    if (comment.getCommentLikesCount() > 0) {
                        comment.decreaseLikesCount();
                        commentRepository.save(comment);  // 좋아요 수 반영
                    }

                    // 응답 반환
                    return CommentLikesResponse.from(comment);
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock(); // 락 해제
                        log.info("락 해제 성공: commentId={}, email={}", commentId, email);
                    }
                }
            } else {
                // 락 획득 실패 로그
                log.warn("락 획득 실패: commentId={}, email={}", commentId, email);
                throw new RuntimeException("좋아요 취소 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            }
        } catch (InterruptedException e) {
            log.error("좋아요 취소 처리 중 오류가 발생했습니다.", e);
            throw new RuntimeException("좋아요 취소 처리 중 오류가 발생했습니다.", e);
        }
    }
}
