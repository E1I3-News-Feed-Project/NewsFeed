package com.nbacm.newsfeed.domain.likes.service;

import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.feed.repository.FeedRepository;
import com.nbacm.newsfeed.domain.likes.dto.response.CommentLikesResponse;
import com.nbacm.newsfeed.domain.likes.dto.response.FeedLikesResponse;
import com.nbacm.newsfeed.domain.likes.entity.CommentLikes;
import com.nbacm.newsfeed.domain.likes.entity.FeedLikes;
import com.nbacm.newsfeed.domain.likes.repositroy.CommentLikesRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentLikesService {

    private final CommentLikesRepository commentLikesRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentLikesResponse likeComment(Long commentId, HttpServletRequest request) {
        String email = (String) request.getAttribute("AuthenticatedUser");
        User user = userRepository.finByEmailOrElseThrow(email);
        Comment comment = commentRepository.findById(commentId).orElseThrow(RuntimeException::new);

        if (commentLikesRepository.existsByUserAndComment(user, comment)) {
            throw new RuntimeException("이미 좋아요를 누른 댓글입니다.");
        }

        CommentLikes commentLikes = CommentLikes.builder()
                .user(user)
                .comment(comment)
                .build();

        commentLikesRepository.save(commentLikes);

        comment.increaseLikesCount();

        return CommentLikesResponse.from(comment);
    }

    @Transactional
    public CommentLikesResponse unlikeComment(Long commentId, HttpServletRequest request) {
        String email = (String) request.getAttribute("AuthenticatedUser");
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
