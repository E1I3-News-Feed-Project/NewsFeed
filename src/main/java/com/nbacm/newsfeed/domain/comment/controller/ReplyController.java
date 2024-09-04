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

    @PostMapping("/{commentId}")
    ResponseEntity<ReplyResponseDto> createCoComment (@PathVariable Long commentId,
                                                      HttpServletRequest request,
                                                      @RequestBody ReplyRequestDto commentRequestDto){
        String email = (String) request.getAttribute("AuthenticatedUser");
        try{
            ReplyResponseDto comment = replyService.ReplyComments(commentId, email, commentRequestDto);
            return ResponseEntity.ok(comment);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{commentId}")
    ResponseEntity<ReplyResponseDto> updateCoComment(@PathVariable Long commentId, HttpServletRequest request, @RequestBody ReplyRequestDto commentRequestDto){
        String email = (String) request.getAttribute("AuthenticatedUser");
        try {
            ReplyResponseDto comment = replyService.updateReplyComments(commentId, email, commentRequestDto);
            return ResponseEntity.ok(comment);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{commentId}")
    ResponseEntity<List<ReplyResponseDto>> getCoComment(@PathVariable Long commentId){
        try{
            List<ReplyResponseDto> getAllCoComment = replyService.getAllReply(commentId);
            return ResponseEntity.ok(getAllCoComment);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{replyId}")
    ResponseEntity<ReplyResponseDto> deleteCoComment(@PathVariable Long replyId, HttpServletRequest request){
        String email = (String) request.getAttribute("AuthenticatedUser");
        try{
            ReplyResponseDto comment = replyService.deleteReply(replyId,email);
            return ResponseEntity.ok(comment);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
