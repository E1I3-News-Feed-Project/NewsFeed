package com.nbacm.newsfeed.domain.comment.service;

import com.nbacm.newsfeed.domain.comment.dto.request.CoCommentRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.response.CoCommentResponseDto;
import com.nbacm.newsfeed.domain.comment.entity.CoComment;
import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.comment.repository.CoCommentRepository;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class CoCommentServiceImpl implements CoCommentService {
    private final CoCommentRepository coCommentRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CoCommentResponseDto createCoComments(Long commentId, String email, CoCommentRequestDto commentRequestDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NullPointerException("해당 댓글을 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("해당 유저를 찾을 수 없습니다."));
        CoComment saveComment = new CoComment(commentRequestDto, comment, user);
        CoComment savedCoComment = coCommentRepository.save(saveComment);
        return new CoCommentResponseDto(savedCoComment);
    }

    @Transactional
    public CoCommentResponseDto updateCoComments(Long commentId, String email, CoCommentRequestDto coCommentRequestDto) {
        CoComment coComment = coCommentRepository.findById(commentId).orElseThrow(() -> new NullPointerException("해당 댓글을 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("해당 유저를 찾을 수 없습니다."));
        if (!coComment.getEmail().equals(email)) {
            throw new IllegalArgumentException("삭제 할 권한이 없습니다.");

        }
        coComment.update(coCommentRequestDto, coComment, user);
        CoComment savedCoComment = coCommentRepository.save(coComment);
        return new CoCommentResponseDto(savedCoComment);
    }

    @Transactional(readOnly = true)
    public List<CoCommentResponseDto> getAllCoComment(Long commentId) {
        List<CoComment> coComments = coCommentRepository.findByParentCommentCommentId(commentId);
        System.out.println("Fetched CoComments: " + coComments);  // 로그 추가

        return coComments.stream().map(CoCommentResponseDto::new).toList();
    }

    @Transactional
    public CoCommentResponseDto deleteCoComment(Long cocommentId, String email) {
        CoComment coComment = coCommentRepository.findById(cocommentId).orElseThrow(() -> new NullPointerException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException("유저를 찾을 수 없습니다."));
        System.out.println(coComment.getEmail() + " " + user.getEmail());
        if (!coComment.getEmail().equals(email)) {
            throw new IllegalArgumentException("삭제 할 권한이 없습니다.");

        }
        coCommentRepository.delete(coComment);
        return new CoCommentResponseDto(coComment);
    }
}
