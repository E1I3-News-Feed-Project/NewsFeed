package com.nbacm.newsfeed.domain.comment.service;

import com.nbacm.newsfeed.domain.comment.dto.request.CoCommentRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.response.CoCommentResponseDto;

import java.util.List;

public interface CoCommentService {

    CoCommentResponseDto createCoComments(Long commentId, String email, CoCommentRequestDto commentRequestDto);

    CoCommentResponseDto updateCoComments(Long commentId, String email, CoCommentRequestDto coCommentRequestDto);

    List<CoCommentResponseDto> getAllCoComment(Long commentId);

    CoCommentResponseDto deleteCoComment(Long cocommentId, String email);

}
