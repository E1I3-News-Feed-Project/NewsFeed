package com.nbacm.newsfeed.domain.comment.controller;

import com.nbacm.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.response.CommentResponseDto;
import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.comment.service.CommentService;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.feed.repository.FeedRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
private final CommentService commentService;
    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;

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

    @GetMapping("/{commentId}")
    ResponseEntity<CommentResponseDto> getComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getComment(commentId));
    }

    @GetMapping
    ResponseEntity<List<CommentResponseDto>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComment());
    }

    @GetMapping("/feeds/{feedId}")
    ResponseEntity<List<CommentResponseDto>> getFeedComments(@PathVariable Long feedId) {
        return ResponseEntity.ok(commentService.getFeedComment(feedId));
    }

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

    @DeleteMapping("/{commentId}")
    ResponseEntity<CommentResponseDto> deleteComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.deleteComments(commentId));
    }
}
