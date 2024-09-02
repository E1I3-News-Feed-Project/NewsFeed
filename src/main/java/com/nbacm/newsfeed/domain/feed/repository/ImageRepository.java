package com.nbacm.newsfeed.domain.feed.repository;

import com.nbacm.newsfeed.domain.feed.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Modifying
    @Query("DELETE FROM Image i WHERE i.feed.feedId = :feedId")
    void deleteByFeedId(@Param("feedId") Long feedId);
}
