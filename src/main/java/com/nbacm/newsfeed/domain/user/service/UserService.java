package com.nbacm.newsfeed.domain.user.service;

import com.nbacm.newsfeed.domain.user.dto.request.UserLoginRequestDto;
import com.nbacm.newsfeed.domain.user.dto.request.UserRequestDto;
import com.nbacm.newsfeed.domain.user.dto.response.MyPageUserResponseDto;
import com.nbacm.newsfeed.domain.user.dto.response.UserResponseDto;
import com.nbacm.newsfeed.domain.user.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    UserResponseDto signup(UserRequestDto userRequestDto, MultipartFile profileImage)  throws IOException;

    String saveProfileImage(MultipartFile profileImage, String email) throws IOException;

    String login(UserLoginRequestDto userLoginRequestDto);

    Resource loadProfileImage(String email) throws IOException;

    UserResponseDto updateUser(String email,UserRequestDto userRequestDto,MultipartFile profileImage) throws IOException;

    void deleteExistingProfileImage(User user);

    String logout(String accessToken);

    void deleteAccount(String email, String password);

    MyPageUserResponseDto getUser(String email);

}
