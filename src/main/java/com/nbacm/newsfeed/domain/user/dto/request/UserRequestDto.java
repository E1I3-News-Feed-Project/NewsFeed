package com.nbacm.newsfeed.domain.user.dto.request;

import com.nbacm.newsfeed.domain.user.dto.response.UserResponseDto;
import com.nbacm.newsfeed.domain.user.entity.User;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
public class UserRequestDto {

    private String email;

    private String nickname;

    private String password;

    private MultipartFile profile_image;



}
