package com.nbacm.newsfeed.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @Builder
    public User(Long userId, String email, String password, String nickname, String profile_image) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profile_image = profile_image;
    }

    public void update(String password, String nickname, String profile_image) {
        if (password != null) this.password = password;
        if (nickname != null) this.nickname = nickname;
        if (profile_image != null) this.profile_image = profile_image;
    }
    public void deleteAccount(){
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }


}
