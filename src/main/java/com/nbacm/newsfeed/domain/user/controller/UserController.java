package com.nbacm.newsfeed.domain.user.controller;
import com.nbacm.newsfeed.domain.user.common.utils.JwtUtils;
import com.nbacm.newsfeed.domain.user.dto.request.DeleteAccountRequestDto;
import com.nbacm.newsfeed.domain.user.dto.request.UserLoginRequestDto;
import com.nbacm.newsfeed.domain.user.dto.request.UserRequestDto;
import com.nbacm.newsfeed.domain.user.dto.response.MyPageUserResponseDto;
import com.nbacm.newsfeed.domain.user.dto.response.UserResponseDto;
import com.nbacm.newsfeed.domain.user.exception.NotMatchException;
import com.nbacm.newsfeed.domain.user.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<UserResponseDto> signupUser(@Valid @ModelAttribute UserRequestDto userRequestDto,
                                                      @RequestParam(value = "profileImage",required = false)
                                                      MultipartFile profileImage) throws IOException {
        UserResponseDto user = userService.signup(userRequestDto, profileImage);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        String token = userService.login(userLoginRequestDto);
        return ResponseEntity.ok(token);
    }

    @GetMapping(value = "/profile")
    public ResponseEntity<Resource> getProfileImage(HttpServletRequest request) throws IOException {
        String email =  request.getAttribute("AuthenticatedUser").toString(); // 인증된 사용자 이메일 가져오기
        Resource file = userService.loadProfileImage(email);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @GetMapping("/MyPage")
    public ResponseEntity<MyPageUserResponseDto> getMyPageUsers(HttpServletRequest request) {
        String email = request.getAttribute("AuthenticatedUser").toString();
        MyPageUserResponseDto myPageUserResponseDto =userService.getUser(email);
        return ResponseEntity.ok().body(myPageUserResponseDto);
    }

    @PutMapping(value = "/update",produces = "application/json")
    public ResponseEntity<UserResponseDto> update(@ModelAttribute UserRequestDto userRequestDto,
                                                  @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                                  HttpServletRequest request) throws IOException {
        String email = request.getAttribute("AuthenticatedUser").toString();
        UserResponseDto updatedUser = userService.updateUser(email, userRequestDto, profileImage);
        return ResponseEntity.ok().body(updatedUser);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String expiredToken = userService.logout(token.replace(JwtUtils.BEARER_PREFIX, ""));
        return ResponseEntity.ok()
                .header(JwtUtils.AUTHORIZATION_HEADER, JwtUtils.BEARER_PREFIX + expiredToken)
                .body("로그아웃 되었습니다.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteAccountRequestDto deleteAccountRequestDto,
                                                HttpServletRequest request) {
        String email = request.getAttribute("AuthenticatedUser").toString();
        userService.deleteAccount(email, deleteAccountRequestDto.getPassword());
        return ResponseEntity.ok("계정이 성공적으로 삭제되었습니다.");
    }



}
