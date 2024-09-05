package com.nbacm.newsfeed.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginRequestDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

}
