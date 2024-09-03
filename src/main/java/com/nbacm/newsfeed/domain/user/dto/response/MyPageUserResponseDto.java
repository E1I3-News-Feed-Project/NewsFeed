package com.nbacm.newsfeed.domain.user.dto.response;

import com.nbacm.newsfeed.domain.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyPageUserResponseDto {

    private String email;

    private String nickname;

    private String profileImageUrl;

    public static MyPageUserResponseDto from(User user, String imageUrl) {
        MyPageUserResponseDto dto = new MyPageUserResponseDto();
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.profileImageUrl =imageUrl;
        return dto;
    }
}
