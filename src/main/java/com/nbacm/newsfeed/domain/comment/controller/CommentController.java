package com.nbacm.newsfeed.domain.comment.controller;

import com.nbacm.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.response.CommentResponseDto;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.comment.service.CommentServiceImpl;
import com.nbacm.newsfeed.domain.feed.repository.FeedRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
private final CommentServiceImpl commentService;

    //댓글 작성
    @PostMapping("/{feedId}")
    ResponseEntity<CommentResponseDto> createComment(@PathVariable Long feedId,
                                                     HttpServletRequest request,
                                                     @RequestBody CommentRequestDto commentRequestDto) {
        String email = (String) request.getAttribute("AuthenticatedUser");
        try {
            CommentResponseDto comment = commentService.addComment(feedId, email, commentRequestDto);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //댓글 아이디 단건 조회
    @GetMapping("/{commentId}")
    ResponseEntity<CommentResponseDto> getComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getComment(commentId));
    }
    // 댓글 전체 조회
    @GetMapping
    ResponseEntity<List<CommentResponseDto>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComment());
    }
    // 피드 아이디 별 댓글 조회
    @GetMapping("/feeds/{feedId}")
    ResponseEntity<List<CommentResponseDto>> getFeedComments(@PathVariable Long feedId) {
        return ResponseEntity.ok(commentService.getFeedComment(feedId));
    }

    //댓글 수정
    @PutMapping("/{feedId}/{commentId}")
    ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long feedId, @PathVariable Long commentId,
                                                     HttpServletRequest request,
                                                     @RequestBody CommentRequestDto commentRequestDto) {
        String email = (String) request.getAttribute("AuthenticatedUser");
        try{
            CommentResponseDto comment = commentService.updateComments(commentId, feedId, email, commentRequestDto);
            return ResponseEntity.ok(comment);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    ResponseEntity<CommentResponseDto> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        String email = (String) request.getAttribute("AuthenticatedUser");
        try{
            CommentResponseDto comment = commentService.deleteComments(commentId, email);
            return ResponseEntity.ok(comment);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
