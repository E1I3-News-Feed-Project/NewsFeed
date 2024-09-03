package com.nbacm.newsfeed.domain.follow.entity;

import com.nbacm.newsfeed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class FollowRequest {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recevier_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FollowRequestStatus status;

    @Builder
    private FollowRequest(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = FollowRequestStatus.PENDING;
    }

    public void accept() {
        this.status = FollowRequestStatus.ACCEPTED;
    }

    public void reject() {
        this.status = FollowRequestStatus.REJECTED;
    }
}
