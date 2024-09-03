package com.nbacm.newsfeed.domain.likes.controller;

import com.nbacm.newsfeed.domain.likes.dto.response.FeedLikesResponse;
import com.nbacm.newsfeed.domain.likes.service.FeedLikesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
@RestController
public class FeedLikesController {

    private final FeedLikesService feedLikesService;

    @PostMapping("/{feedId}/likes")
    public ResponseEntity<FeedLikesResponse> likeFeed(@PathVariable Long feedId, HttpServletRequest request) {
        FeedLikesResponse feedLikesResponse = feedLikesService.likeFeed(feedId, request);
        return ResponseEntity.ok(feedLikesResponse);
    }

    @DeleteMapping("/{feedId}/likes")
    public ResponseEntity<FeedLikesResponse> unlikeFeed(@PathVariable Long feedId, HttpServletRequest request) {
        FeedLikesResponse feedLikesResponse = feedLikesService.unlikeFeed(feedId, request);
        return ResponseEntity.ok(feedLikesResponse);
    }
}
