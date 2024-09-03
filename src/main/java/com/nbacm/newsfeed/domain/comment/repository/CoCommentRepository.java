package com.nbacm.newsfeed.domain.comment.repository;

import com.nbacm.newsfeed.domain.comment.entity.CoComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoCommentRepository extends JpaRepository<CoComment, Long> {
    List<CoComment> findByParentCommentCommentId(Long commentId);

}
