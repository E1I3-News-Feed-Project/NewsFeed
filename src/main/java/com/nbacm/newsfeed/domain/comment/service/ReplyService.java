package com.nbacm.newsfeed.domain.comment.service;

import com.nbacm.newsfeed.domain.comment.dto.request.ReplyRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.response.ReplyResponseDto;

import java.util.List;

public interface ReplyService {

    ReplyResponseDto ReplyComments(Long commentId, String email, ReplyRequestDto commentRequestDto);

    ReplyResponseDto updateReplyComments(Long commentId, String email, ReplyRequestDto replyRequestDto);

    List<ReplyResponseDto> getAllReply(Long commentId);

    ReplyResponseDto deleteReply(Long cocommentId, String email);

}
