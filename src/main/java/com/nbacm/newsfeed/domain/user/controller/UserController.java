package com.nbacm.newsfeed.domain.user.controller;
import com.nbacm.newsfeed.domain.user.dto.request.UserLoginRequestDto;
import com.nbacm.newsfeed.domain.user.dto.request.UserRequestDto;
import com.nbacm.newsfeed.domain.user.dto.response.UserResponseDto;
import com.nbacm.newsfeed.domain.user.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
}
