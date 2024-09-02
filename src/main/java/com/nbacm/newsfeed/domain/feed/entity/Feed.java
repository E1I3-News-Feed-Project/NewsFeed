package com.nbacm.newsfeed.domain.feed.entity;

import com.nbacm.newsfeed.domain.feed.dto.request.FeedRequestDto;
import com.nbacm.newsfeed.domain.time.entity.BaseTime;
import com.nbacm.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Feed extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedId;

    private String content;

    private int likesCount;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    public Feed(String content, User user, int likesCount) {
        this.content = content;
        this.user = user;
        this.likesCount = likesCount;
    }

    public void update(FeedRequestDto feedRequestDto) {
        this.content = feedRequestDto.getContent();
    }

    public void addImage(Image image) {
        this.images.add(image);
        image.setFeed(this);
    }

    // getAuthor 메서드 추가
    public User getAuthor() {
        return this.user;
    }
}
