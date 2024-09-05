package com.nbacm.newsfeed.domain.likes.controller;

import com.nbacm.newsfeed.domain.likes.dto.response.FeedLikesResponse;
import com.nbacm.newsfeed.domain.likes.service.FeedLikesServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
@RestController
public class FeedLikesController {

    private final FeedLikesServiceImpl feedLikesService;

    @PostMapping("/{feedId}/likes")
    public ResponseEntity<FeedLikesResponse> likeFeed(@PathVariable Long feedId, HttpServletRequest request) {
        String email = request.getAttribute("AuthenticatedUser").toString();
        FeedLikesResponse feedLikesResponse = feedLikesService.likeFeed(feedId, email);
        return ResponseEntity.ok(feedLikesResponse);
    }

    @DeleteMapping("/{feedId}/likes")
    public ResponseEntity<FeedLikesResponse> unlikeFeed(@PathVariable Long feedId, HttpServletRequest request) {
        String email = request.getAttribute("AuthenticatedUser").toString();
        FeedLikesResponse feedLikesResponse = feedLikesService.unlikeFeed(feedId, email);
        return ResponseEntity.ok(feedLikesResponse);
    }
}
