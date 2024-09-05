package com.nbacm.newsfeed.domain.comment.controller;

import com.nbacm.newsfeed.domain.comment.dto.request.ReplyRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.response.ReplyResponseDto;
import com.nbacm.newsfeed.domain.comment.service.ReplyServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/replys")
public class ReplyController {
    private final ReplyServiceImpl replyService;

    //대댓글 작성
    @PostMapping("/{commentId}")
    ResponseEntity<ReplyResponseDto> createReply(@PathVariable Long commentId,
                                                 HttpServletRequest request,
                                                 @RequestBody ReplyRequestDto commentRequestDto) {
        String email = (String) request.getAttribute("AuthenticatedUser");
        ReplyResponseDto comment = replyService.ReplyComments(commentId, email, commentRequestDto);
        return ResponseEntity.ok(comment);
    }

    // 대댓글 수정
    @PutMapping("/{commentId}")
    ResponseEntity<ReplyResponseDto> updateReply(@PathVariable Long commentId, HttpServletRequest request, @RequestBody ReplyRequestDto commentRequestDto) {
        String email = (String) request.getAttribute("AuthenticatedUser");
        ReplyResponseDto comment = replyService.updateReplyComments(commentId, email, commentRequestDto);
        return ResponseEntity.ok(comment);
    }

    // 대댓글 -> 댓글 아이디별 조회
    @GetMapping("/{commentId}")
    ResponseEntity<List<ReplyResponseDto>> getReply(@PathVariable Long commentId) {
        List<ReplyResponseDto> getAllCoComment = replyService.getAllReply(commentId);
        return ResponseEntity.ok(getAllCoComment);
    }

    // 대댓글 삭제
    @DeleteMapping("/{replyId}")
    ResponseEntity<ReplyResponseDto> deleteReply(@PathVariable Long replyId, HttpServletRequest request) {
        String email = (String) request.getAttribute("AuthenticatedUser");
        ReplyResponseDto comment = replyService.deleteReply(replyId, email);
        return ResponseEntity.ok(comment);
    }

}
