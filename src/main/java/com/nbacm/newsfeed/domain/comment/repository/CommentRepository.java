package com.nbacm.newsfeed.domain.comment.repository;

import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@RequestMapping
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c join fetch c.user where c.commentId = :commentId")
    Optional<Comment> findByCommentId(@Param("commentId") Long commentId);

    List<Comment> findAllByOrderByUpdatedAtDesc();
    List<Comment> findByFeedOrderByUpdatedAtDesc(Feed feed);
}



