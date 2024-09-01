package com.nbacm.newsfeed.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Builder;
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
    private String password;

    @NotNull
    private String nickname;

    private String profile_image;


    @Builder
    public User(String email, String password, String nickName, String profile_image) {
        this.email = email;
        this.password = password;
        this.nickname = nickName;
        this.profile_image = profile_image;
    }


}
