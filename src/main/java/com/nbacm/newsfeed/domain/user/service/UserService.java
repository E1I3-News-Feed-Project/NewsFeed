package com.nbacm.newsfeed.domain.user.service;

import com.nbacm.newsfeed.domain.user.dto.request.UserLoginRequestDto;
import com.nbacm.newsfeed.domain.user.dto.request.UserRequestDto;
import com.nbacm.newsfeed.domain.user.dto.response.UserResponseDto;
import com.nbacm.newsfeed.domain.user.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    UserResponseDto signUp(UserRequestDto userRequestDto, MultipartFile profile_image)  throws IOException;

    String saveProfileImage(MultipartFile profile_image, String email) throws IOException;

    String login(UserLoginRequestDto userLoginRequestDto);

    Resource loadProfileImage(String email) throws IOException;

}
