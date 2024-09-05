package com.nbacm.newsfeed.domain.user.dto.response;

import com.nbacm.newsfeed.domain.user.entity.User;
import lombok.Data;

@Data
public class UserResponseDto {

    private String email;

    private String nickName;

    private String profileImage;

    public UserResponseDto(String email, String nickName, String profileImage) {
        this.email = email;
        this.nickName = nickName;
        this.profileImage = profileImage;
    }

    public static UserResponseDto from(User user) {

        return new UserResponseDto(
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage()
        );
    }
}
