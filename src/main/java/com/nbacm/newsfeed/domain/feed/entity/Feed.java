package com.nbacm.newsfeed.domain.feed.entity;

import com.nbacm.newsfeed.domain.time.entity.BaseTime;
import com.nbacm.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Feed extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedId;

    private String content;

    private int likesCount;

    @ManyToOne
    private User user;

}
