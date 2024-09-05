package com.nbacm.newsfeed.domain.follow.controller;

import com.nbacm.newsfeed.domain.follow.dto.FollowRequestResponse;
import com.nbacm.newsfeed.domain.follow.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class FollowController {

    private final FollowService followService;

    @DeleteMapping("/users/{userId}/follow")
    public ResponseEntity<String> unfollow(@PathVariable Long userId, HttpServletRequest request) {
        String email = request.getAttribute("AuthenticatedUser").toString();
        followService.deleteFollow(userId,email);
        return ResponseEntity.ok("언팔 완료");
    }

    @PostMapping("/users/{userId}/follow")
    public ResponseEntity<FollowRequestResponse> sendFollowRequest(@PathVariable Long userId, HttpServletRequest request) {
        String email = request.getAttribute("AuthenticatedUser").toString();
        FollowRequestResponse followRequestResponse = followService.sendFollowRequest(userId, email);
        return ResponseEntity.ok(followRequestResponse);
    }

    @PostMapping("/follows/{followRequestId}/accept")
    public ResponseEntity<FollowRequestResponse> acceptFollowRequest(@PathVariable Long followRequestId, HttpServletRequest request) {
        String email = request.getAttribute("AuthenticatedUser").toString();
        FollowRequestResponse followRequestResponse = followService.acceptFollowRequest(followRequestId, email);
        return ResponseEntity.ok(followRequestResponse);
    }

    @PostMapping("/follows/{followRequestId}/reject")
    public ResponseEntity<FollowRequestResponse> rejectFollowRequest(@PathVariable Long followRequestId, HttpServletRequest request) {
        String email = request.getAttribute("AuthenticatedUser").toString();
        FollowRequestResponse followRequestResponse = followService.rejectFollowRequest(followRequestId,email);
        return ResponseEntity.ok(followRequestResponse);
    }

}
