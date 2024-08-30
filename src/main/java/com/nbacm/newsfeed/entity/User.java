package com.nbacm.newsfeed.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Pattern(regexp = "(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,16}",
            message = "비밀번호는 8~16자 영문 소문자, 숫자, 특수문자를 포함 하여야 합니다.")
    private String password;

    @NotNull
    private String nickName;

    private String profile_image;


}
