package com.nbacm.newsfeed.domain.feed.repository;

import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    // 이메일로 작성자의 뉴스피드를 조회하는 메서드
    @Query("SELECT DISTINCT f FROM Feed f " +
            "LEFT JOIN FETCH f.images " +
            "LEFT JOIN FETCH f.user " +
            "WHERE f.user.email = :email")
    Page<Feed> findByUserEmailOrderByCreatedAtDesc(@Param("email") String email, Pageable pageable);

    @Query("SELECT DISTINCT f FROM Feed f " +
            "LEFT JOIN FETCH f.images " +
            "LEFT JOIN FETCH f.user " +
            "WHERE f.feedId = :feedId")
    Optional<Feed> findByIdWithImagesAndUser(@Param("feedId") Long feedId);

    @Query("SELECT f FROM Feed f LEFT JOIN FETCH f.images WHERE f.user IN :users ORDER BY f.createdAt DESC")
    List<Feed> findByUserInOrderByCreatedAtDesc(@Param("users") List<User> users);
}
