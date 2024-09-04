package com.nbacm.newsfeed.domain.comment.service;

import com.nbacm.newsfeed.domain.comment.dto.request.ReplyRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.response.ReplyResponseDto;
import com.nbacm.newsfeed.domain.comment.entity.ReplyComment;
import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.comment.repository.ReplyRepository;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.exception.NotFoundException;
import com.nbacm.newsfeed.domain.exception.UnauthorizedException;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReplyResponseDto ReplyComments(Long commentId, String email, ReplyRequestDto replyRequestDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("해당 댓글을 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));
        ReplyComment saveComment = new ReplyComment(replyRequestDto, comment, user);
        ReplyComment savedReplyComment = replyRepository.save(saveComment);
        comment.incrementReplyCount();
        commentRepository.save(comment);
        return new ReplyResponseDto(savedReplyComment);
    }

    @Override
    @Transactional
    public ReplyResponseDto updateReplyComments(Long commentId, String email, ReplyRequestDto replyRequestDto) {
        ReplyComment replyComment = replyRepository.findById(commentId).orElseThrow(() -> new NotFoundException("해당 댓글을 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));
        if (!replyComment.getEmail().equals(email)) {
            throw new IllegalArgumentException("삭제 할 권한이 없습니다.");

        }
        replyComment.update(replyRequestDto, replyComment, user);
        ReplyComment savedReplyComment = replyRepository.save(replyComment);
        return new ReplyResponseDto(savedReplyComment);
    }

    @Override
    public List<ReplyResponseDto> getAllReply(Long commentId) {
        List<ReplyComment> replyComments = replyRepository.findByParentCommentCommentId(commentId);
        return replyComments.stream().map(ReplyResponseDto::new).toList();
    }

    @Override
    @Transactional
    public ReplyResponseDto deleteReply(Long cocommentId, String email) {
        ReplyComment replyComment = replyRepository.findById(cocommentId).orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        System.out.println(replyComment.getEmail() + " " + user.getEmail());
        if (!replyComment.getEmail().equals(email)) {
            throw new UnauthorizedException("삭제 할 권한이 없습니다.");

        }
        Comment parentComment = replyComment.getParentComment();
        parentComment.decrementReplyCount();
        commentRepository.save(parentComment);
        replyRepository.delete(replyComment);
        return new ReplyResponseDto(replyComment);
    }
}
