package com.nbacm.newsfeed.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JsonIgnore
    private String password;

    @NotNull
    private String nickname;

    private String profileImage;

    private boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @Builder
    public User(Long userId, String email, String password, String nickname, String profileImage) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public void update(String password, String nickname, String profileImage) {
        if (password != null) this.password = password;
        if (nickname != null) this.nickname = nickname;
        if (profileImage != null) this.profileImage = profileImage;
    }
    public void deleteAccount(){
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }


}
