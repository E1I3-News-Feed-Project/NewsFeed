package com.nbacm.newsfeed.domain.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginResponseDto {

    private String token;

    public UserLoginResponseDto(String token) {
        this.token = token;
    }

    public static UserLoginResponseDto getLoginResponseDto(String token){
        return new UserLoginResponseDto(token);
    }
}