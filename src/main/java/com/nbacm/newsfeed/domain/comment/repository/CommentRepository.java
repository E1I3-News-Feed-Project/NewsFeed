package com.nbacm.newsfeed.domain.comment.repository;

import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByFeed(Feed feed);
}
