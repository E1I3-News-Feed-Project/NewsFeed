package com.nbacm.newsfeed.domain.follow.entity;

import com.nbacm.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Follow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followerId;

    private Long followeeId;

    @ManyToOne
    private User user;



}
