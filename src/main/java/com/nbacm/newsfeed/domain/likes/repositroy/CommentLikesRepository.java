package com.nbacm.newsfeed.domain.likes.repositroy;

import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.likes.entity.CommentLikes;
import com.nbacm.newsfeed.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {

    boolean existsByUserAndComment(User user, Comment comment);

    void deleteByUserAndComment(User user, Comment comment);

    long countByComment(Comment comment);

}
