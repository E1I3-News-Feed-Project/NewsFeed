package com.nbacm.newsfeed.domain.follow.controller;

import com.nbacm.newsfeed.domain.follow.dto.FollowRequestResponse;
import com.nbacm.newsfeed.domain.follow.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/follows")
@RestController
public class FollowController {

    private final FollowService followService;

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> unfollow(@PathVariable Long userId, HttpServletRequest request) {
        followService.deleteFollow(userId, request);
        return ResponseEntity.ok("언팔 완료");
    }

    @PostMapping("/{userId}")
    public ResponseEntity<FollowRequestResponse> sendFollowRequest(@PathVariable Long userId, HttpServletRequest request) {
        FollowRequestResponse followRequestResponse = followService.sendFollowRequest(userId, request);
        return ResponseEntity.ok(followRequestResponse);
    }

    @PostMapping("/accept/{followRequestId}")
    public ResponseEntity<FollowRequestResponse> acceptFollowRequest(@PathVariable Long followRequestId, HttpServletRequest request) {
        FollowRequestResponse followRequestResponse = followService.acceptFollowRequest(followRequestId, request);
        return ResponseEntity.ok(followRequestResponse);
    }

    @PostMapping("/reject/{followRequestId}")
    public ResponseEntity<FollowRequestResponse> rejectFollowRequest(@PathVariable Long followRequestId, HttpServletRequest request) {
        FollowRequestResponse followRequestResponse = followService.rejectFollowRequest(followRequestId, request);
        return ResponseEntity.ok(followRequestResponse);
    }

}
