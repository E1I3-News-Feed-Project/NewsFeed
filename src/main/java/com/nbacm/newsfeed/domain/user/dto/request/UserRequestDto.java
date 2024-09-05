package com.nbacm.newsfeed.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
public class UserRequestDto {

    private String email;

    private String nickname;

    @Pattern(
            regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}",
            message = "비밀번호는 최소 8자 이상이어야 하며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1글자씩 포함해야 합니다."
    )
    private String password;

    private MultipartFile profileImage;


    public UserRequestDto(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
