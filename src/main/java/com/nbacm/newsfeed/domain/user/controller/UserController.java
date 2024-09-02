package com.nbacm.newsfeed.domain.user.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbacm.newsfeed.domain.user.dto.request.UserLoginRequestDto;
import com.nbacm.newsfeed.domain.user.dto.request.UserRequestDto;
import com.nbacm.newsfeed.domain.user.dto.response.UserResponseDto;
import com.nbacm.newsfeed.domain.user.exception.NotMatchException;
import com.nbacm.newsfeed.domain.user.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")

public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> signupUser(@Valid @ModelAttribute UserRequestDto userRequestDto,
                                                      @RequestParam(value = "profile_image",required = false) MultipartFile profile_image) {
        try {
            UserResponseDto user = userService.signUp(userRequestDto, profile_image);
            return ResponseEntity.ok(user);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        String token = userService.login(userLoginRequestDto);
        return ResponseEntity.ok(token);
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<Resource> getProfileImage(HttpServletRequest request) {
        String email = (String) request.getAttribute("AuthenticatedUser"); // 인증된 사용자 이메일 가져오기
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        try {
            Resource file = userService.loadProfileImage(email);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PutMapping(value = "/update")
    public ResponseEntity<UserResponseDto> update(@ModelAttribute UserRequestDto userRequestDto,
                                                  @RequestPart(value = "profile_image", required = false) MultipartFile profileImage,
                                                  HttpServletRequest request) {
        try {
            String email = (String) request.getAttribute("AuthenticatedUser");
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            UserResponseDto updatedUser = userService.updateUser(email, userRequestDto, profileImage);
            return ResponseEntity.ok()
                    .body(updatedUser);

        } catch (NotMatchException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



}
