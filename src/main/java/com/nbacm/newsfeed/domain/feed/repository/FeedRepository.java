package com.nbacm.newsfeed.domain.feed.repository;

import com.nbacm.newsfeed.domain.feed.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    // 이메일로 작성자의 뉴스피드를 조회하는 메서드
    Page<Feed> findByUserEmailOrderByCreatedAtDesc(String email, Pageable pageable);
}
