package com.nbacm.newsfeed.domain.feed.controller;

import com.nbacm.newsfeed.domain.feed.dto.request.FeedRequestDto;
import com.nbacm.newsfeed.domain.feed.dto.response.FeedResponseDto;
import com.nbacm.newsfeed.domain.feed.service.FeedService;
import com.nbacm.newsfeed.domain.user.common.utils.JwtUtils;
import com.nbacm.newsfeed.domain.user.exception.NotMatchException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

        String token = extractToken(request);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT 토큰이 누락되었습니다.");
        }

        try {
            if (!jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
            }

            String email = jwtUtils.getUserEmailFromToken(token);
            FeedRequestDto requestDto = new FeedRequestDto(content, images, email);

            feedService.createFeed(requestDto);
            return ResponseEntity.ok("게시물이 성공적으로 작성되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 작성에 실패하였습니다.");
        }
    }

    @GetMapping("/{feed_id}")
    public ResponseEntity<FeedResponseDto> getFeedById(@PathVariable Long feed_id, HttpServletRequest request) {
        String token = extractToken(request);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // JWT 토큰이 누락되었습니다.
        }

        if (!jwtUtils.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 유효하지 않은 토큰입니다.
        }

        String email = jwtUtils.getUserEmailFromToken(token);
        Optional<FeedResponseDto> feedResponse = feedService.getFeedById(feed_id, email);

        return feedResponse
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)); // 게시물을 찾을 수 없습니다.
    }

    @GetMapping
    public ResponseEntity<Page<FeedResponseDto>> getAllFeeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        String token = extractToken(request);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // JWT 토큰 누락
        }

        if (!jwtUtils.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 유효하지 않은 토큰
        }

        String email = jwtUtils.getUserEmailFromToken(token);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FeedResponseDto> feedPage = feedService.findFeedsByUser(email, pageable);
        return ResponseEntity.ok(feedPage);
    }

    @PutMapping(value = "/{feed_id}")
    public ResponseEntity<String> updateFeed(
            @PathVariable Long feed_id,
            @ModelAttribute FeedRequestDto feedRequestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest request) {

        try {
            String email = (String) request.getAttribute("AuthenticatedUser");
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 인증 정보가 없습니다.");
            }

            // 이메일을 DTO에 설정
            feedRequestDto.setEmail(email);

            // FeedService 호출
            FeedResponseDto updated = feedService.updateFeed(feed_id, feedRequestDto, images, email);
            return ResponseEntity.ok("게시물을 업데이트했습니다.");

        } catch (NotMatchException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물을 업데이트하지 못했습니다.");
        }
    }

    @DeleteMapping("/{feed_id}")
    public ResponseEntity<String> deleteFeed(@PathVariable Long feed_id, HttpServletRequest request) {
        try {
            // 인증된 사용자 이메일 추출
            String email = (String) request.getAttribute("AuthenticatedUser");
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 인증 정보가 없습니다.");
            }

            // Feed 삭제 서비스 호출
            boolean deleted = feedService.deleteFeed(feed_id, email);
            return deleted ? ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.") :
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");

        } catch (Exception e) {
            // 일반적인 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 삭제 실패.");
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(JwtUtils.AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(JwtUtils.BEARER_PREFIX)) {
            return header.substring(JwtUtils.BEARER_PREFIX.length());
        }
        return null;
    }
}