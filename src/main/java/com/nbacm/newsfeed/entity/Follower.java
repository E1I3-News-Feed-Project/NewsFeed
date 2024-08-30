package com.nbacm.newsfeed.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Follower {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followerId;

    private Long followeeId;

    @ManyToOne
    private User user;



}
