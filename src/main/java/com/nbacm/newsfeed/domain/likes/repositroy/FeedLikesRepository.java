package com.nbacm.newsfeed.domain.likes.repositroy;

import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.likes.entity.FeedLikes;
import com.nbacm.newsfeed.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedLikesRepository extends JpaRepository<FeedLikes, Long> {

    boolean existsByUserAndFeed(User user, Feed feed);

    void deleteByUserAndFeed(User user, Feed feed);
}
