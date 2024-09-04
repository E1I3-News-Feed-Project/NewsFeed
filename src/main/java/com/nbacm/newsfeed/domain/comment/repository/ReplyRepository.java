package com.nbacm.newsfeed.domain.comment.repository;

import com.nbacm.newsfeed.domain.comment.entity.ReplyComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyComment, Long> {
    List<ReplyComment> findByParentCommentCommentId(Long commentId);

}
