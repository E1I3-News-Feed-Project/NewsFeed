package com.nbacm.newsfeed.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Feed {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedId;

    private String content;

    private int likesCount;

    @ManyToOne
    private User user;

}
