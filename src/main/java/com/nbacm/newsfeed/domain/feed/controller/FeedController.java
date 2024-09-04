package com.nbacm.newsfeed.domain.feed.controller;

import com.nbacm.newsfeed.domain.feed.dto.request.FeedRequestDto;
import com.nbacm.newsfeed.domain.feed.dto.response.FeedResponseDto;
import com.nbacm.newsfeed.domain.feed.service.FeedService;
import com.nbacm.newsfeed.domain.user.common.utils.JwtUtils;
import com.nbacm.newsfeed.domain.user.exception.NotMatchException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<String> createFeed(
            @RequestParam String content,
            @RequestParam(required = false) List<MultipartFile> images,
            HttpServletRequest request) {

        try {
            String email = request.getAttribute("AuthenticatedUser").toString();
            FeedRequestDto requestDto = new FeedRequestDto(content, images, email);

            feedService.createFeed(requestDto);
            return ResponseEntity.ok("게시물이 성공적으로 작성되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 작성에 실패하였습니다.");
        }
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<?> getFeedById(@PathVariable Long feedId, HttpServletRequest request) {

        String email = request.getAttribute("AuthenticatedUser").toString();

        Optional<FeedResponseDto> feedResponse = feedService.getFeedById(feedId, email);

        return feedResponse
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)); // 게시물을 찾을 수 없습니다.
    }

    @GetMapping
    public ResponseEntity<List<FeedResponseDto>> getAllFeeds(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        String email = request.getAttribute("AuthenticatedUser").toString();

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<FeedResponseDto> feeds = feedService.getAllFeeds(email, pageable);

        return ResponseEntity.ok(feeds);
    }

    @GetMapping("/followedUsers")
    public ResponseEntity<List<FeedResponseDto>> getFeedsFromFollowedUsers(HttpServletRequest request) {
        String email = request.getAttribute("AuthenticatedUser").toString();
        List<FeedResponseDto> feeds = feedService.getFeedsFromFollowedUsers(email);
        return ResponseEntity.ok(feeds);
    }

    @PutMapping(value = "/{feedId}")
    public ResponseEntity<String> updateFeed(
            @PathVariable Long feedId,
            @ModelAttribute FeedRequestDto feedRequestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest request) {

        try {
            String email = request.getAttribute("AuthenticatedUser").toString();

            // 이메일을 DTO에 설정
            FeedRequestDto dtoWithEmail = new FeedRequestDto(
                    feedRequestDto.getContent(),
                    images,
                    email // 이메일을 생성자로 설정
            );

            // FeedService 호출
            FeedResponseDto updated = feedService.updateFeed(feedId, dtoWithEmail, images, email);
            return ResponseEntity.ok("게시물을 업데이트했습니다.");

        } catch (NotMatchException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물을 업데이트하지 못했습니다.");
        }
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<String> deleteFeed(@PathVariable Long feedId, HttpServletRequest request) {
        try {
            // 인증된 사용자 이메일 추출
            String email = request.getAttribute("AuthenticatedUser").toString();

            // Feed 삭제 서비스 호출
            boolean deleted = feedService.deleteFeed(feedId, email);
            return deleted ? ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.") :
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");

        } catch (Exception e) {
            // 일반적인 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 삭제 실패.");
        }
    }
}