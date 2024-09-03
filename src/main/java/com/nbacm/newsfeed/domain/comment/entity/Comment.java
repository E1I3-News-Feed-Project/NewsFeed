package com.nbacm.newsfeed.domain.comment.entity;

import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.time.entity.BaseTime;
import com.nbacm.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Comment extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @OneToMany(mappedBy = "parent",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Comment> relies = new ArrayList<>();

    private int  commentLikesCount;


}
