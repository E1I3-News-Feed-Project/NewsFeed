package com.nbacm.newsfeed.domain.likes.entity;

import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
public class FeedLikes {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedLikesId;

    @ManyToOne
    private User user;

    @ManyToOne
    private Feed feed;

    @Builder
    private FeedLikes(User user, Feed feed) {
        this.user = user;
        this.feed = feed;
    }
}
