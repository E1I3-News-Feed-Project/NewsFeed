package com.nbacm.newsfeed.domain.likes.controller;

import com.nbacm.newsfeed.domain.likes.dto.response.CommentLikesResponse;
import com.nbacm.newsfeed.domain.likes.service.CommentLikesService;
import com.nbacm.newsfeed.domain.likes.service.CommentLikesServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@RestController
public class CommentLikesController {

    private final CommentLikesServiceImpl commentLikesService;

    @PostMapping("/{commentId}/likes")
    public ResponseEntity<CommentLikesResponse> likeComment(@PathVariable Long commentId, HttpServletRequest request) {
        String email = request.getAttribute("AuthenticatedUser").toString();
        CommentLikesResponse commentLikesResponse = commentLikesService.likeComment(commentId, email);
        return ResponseEntity.ok(commentLikesResponse);
    }

    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<CommentLikesResponse> unlikeComment(@PathVariable Long commentId, HttpServletRequest request) {
        String email = request.getAttribute("AuthenticatedUser").toString();
        CommentLikesResponse commentLikesResponse = commentLikesService.unlikeComment(commentId, email);
        return ResponseEntity.ok(commentLikesResponse);
    }
}
