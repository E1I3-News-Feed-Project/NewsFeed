package com.nbacm.newsfeed.domain.likes.service;

import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.likes.dto.response.CommentLikesResponse;
import com.nbacm.newsfeed.domain.likes.entity.CommentLikes;
import com.nbacm.newsfeed.domain.likes.repositroy.CommentLikesRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
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
    int count  =0;

    @Transactional
    @Override
    public CommentLikesResponse likeComment(Long commentId, String email) {
        // Redisson을 이용해 commentId 기반으로 분산 락을 걸기
        RLock lock = redissonClient.getLock("commentLikeLock:" + commentId);
        count++;
        try {
            // 락을 최대 3초 동안 시도하며, 락을 잡으면 5초 동안 유지
            if (lock.tryLock(3, 5, TimeUnit.SECONDS)) {
                log.info("count:{}",count);
                // 사용자와 댓글을 조회
                User user = userRepository.finByEmailOrElseThrow(email);
                Comment comment = commentRepository.findById(commentId).orElseThrow(RuntimeException::new);
                log.info("id:{}",user.getUserId());

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
                log.info("end:{}",user.getUserId());


                // 응답 반환
                return CommentLikesResponse.from(comment);
            } else {
                throw new RuntimeException("잠시 후 다시 시도해 주세요.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("좋아요 처리 중 오류가 발생했습니다.", e);
        } finally {
            lock.unlock(); // 락 해제
        }

    }

    @Transactional
    @Override
    public CommentLikesResponse unlikeComment(Long commentId, String email) {
        User user = userRepository.finByEmailOrElseThrow(email);
        Comment comment = commentRepository.findById(commentId).orElseThrow(RuntimeException::new);

        if (!commentLikesRepository.existsByUserAndComment(user, comment)) {
            throw new RuntimeException("관련된 정보가 없습니다.");
        }

        commentLikesRepository.deleteByUserAndComment(user, comment);

        comment.decreaseLikesCount();

        return CommentLikesResponse.from(comment);
    }
}
