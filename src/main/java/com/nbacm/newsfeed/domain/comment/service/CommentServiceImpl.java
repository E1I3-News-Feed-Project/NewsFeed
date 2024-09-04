package com.nbacm.newsfeed.domain.comment.service;

import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.response.CommentResponseDto;
import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.feed.repository.FeedRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;

    @Override
    @Transactional
    public CommentResponseDto addComment(Long feedId, String email, CommentRequestDto commentRequestDto) {
        // 게시글 id 검색
        Feed feed = feedRepository.findById(feedId).orElseThrow(()-> new NullPointerException("해당 작성글이 존재하지 않습니다."));
        // email 검색
        User user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException("해당 유저를 찾을 수 없습니다."));

        // 요청 정보 취합
        Comment comment = new Comment(commentRequestDto, feed, user);
        // 코멘트 저장
        commentRepository.save(comment);
        feedRepository.save(feed);
        return new CommentResponseDto(comment);
    }

    //코멘트 단건 조회
    @Override
    public CommentResponseDto getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new NullPointerException("not found ID"));
        return new CommentResponseDto(comment);
    }

    //코멘트 전체 조회(modifiedAt 기준 내림 차순으로 정렬 필요)
    @Override
    public List<CommentResponseDto> getAllComment() {
        return commentRepository.findAll().stream().map(CommentResponseDto::new).toList();
    }

    //코멘트 수정
    @Override
    @Transactional
    public CommentResponseDto updateComments(Long commentId, Long feedId, String email, CommentRequestDto commentRequestDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new NullPointerException("코멘트를 찾을 수 없습니다."));
        Feed feed  = feedRepository.findById(feedId).orElseThrow(()->new NullPointerException("피드를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException("해당 유저를 찾을 수 없습니다"));
        if (!comment.getEmail().equals(email)) {
            throw new IllegalArgumentException("삭제 할 권한이 없습니다.");

        }
        comment.updateComment(commentRequestDto, feed, comment, user);
        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    // 코맨트 삭제
    @Override
    @Transactional
    public CommentResponseDto deleteComments(Long commentId, String email) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new NullPointerException("코멘트를 찾을 수 없습니다."));
        if (!comment.getEmail().equals(email)) {
            throw new IllegalArgumentException("삭제 할 권한이 없습니다.");

        }
        commentRepository.delete(comment);
        return new CommentResponseDto(comment);
    }

    // feed id 기준 코멘트 조회
    public List<CommentResponseDto> getFeedComment(Long feedId) {
        Feed feed  = feedRepository.findById(feedId).orElseThrow(()->new NullPointerException("게시글을 찾을 수 없습니다."));
        return commentRepository.findByFeed(feed).stream().map(CommentResponseDto::new).toList();
    }
}
