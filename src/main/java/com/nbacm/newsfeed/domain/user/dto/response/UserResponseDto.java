package com.nbacm.newsfeed.domain.user.dto.response;

import com.nbacm.newsfeed.domain.user.entity.User;
import lombok.Data;

@Data
public class UserResponseDto {

    private String email;

    private String password;

    private String nickName;

    private String profile_image;

    public UserResponseDto(String email, String password, String nickName, String profile_image) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.profile_image = profile_image;
    }

    public static UserResponseDto from(User user) {

        return new UserResponseDto(
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getProfile_image()
        );
    }
}
