package com.nbacm.newsfeed.domain.comment.service;

import com.nbacm.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.response.CommentResponseDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto addComment(Long feedId, String email, CommentRequestDto commentRequestDto);

    CommentResponseDto getComment(Long commentId);

    List<CommentResponseDto> getAllComment();

    CommentResponseDto updateComments(Long commentId, Long feedId, String email, CommentRequestDto commentRequestDto);

    CommentResponseDto deleteComments(Long commentId);

    List<CommentResponseDto> getFeedComment(Long feedId);
}
