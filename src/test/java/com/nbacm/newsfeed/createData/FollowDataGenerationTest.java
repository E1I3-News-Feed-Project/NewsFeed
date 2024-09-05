package com.nbacm.newsfeed.createData;

import com.nbacm.newsfeed.domain.follow.dto.FollowRequestResponse;
import com.nbacm.newsfeed.domain.follow.entity.Follow;
import com.nbacm.newsfeed.domain.follow.entity.FollowRequest;
import com.nbacm.newsfeed.domain.follow.entity.FollowRequestStatus;
import com.nbacm.newsfeed.domain.follow.repository.FollowRepository;
import com.nbacm.newsfeed.domain.follow.repository.FollowRequestRepository;
import com.nbacm.newsfeed.domain.follow.service.FollowService;
import com.nbacm.newsfeed.domain.follow.service.FollowServiceImpl;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FollowDataGenerationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowServiceImpl followService;

    @Autowired
    private FollowRequestRepository followRequestRepository;

    @Autowired
    private FollowRepository followRepository;

    private static final int USER_COUNT = 500;
    private static final int MAX_FOLLOW_REQUESTS_PER_USER = 50;
    private static final int MAX_FOLLOWS_PER_USER = 30;
    private Random random = new Random();


    @Test
    void generateRandomFollows() {
        List<User> allUsers = userRepository.findAll();

        for (User follower : allUsers) {
            int followCount = random.nextInt(MAX_FOLLOWS_PER_USER) + 1;

            for (int i = 0; i < followCount; i++) {
                User following = getRandomUser(allUsers, follower);
                try {
                    if (!followRepository.existsByFollowerUserIdAndFollowingUserId(follower.getUserId(), following.getUserId())) {
                        Follow follow = Follow.builder()
                                .follower(follower)
                                .following(following)
                                .build();
                        followRepository.save(follow);
                        System.out.println("팔로우 관계 생성: 팔로워=" + follower.getEmail() + ", 팔로잉=" + following.getEmail());
                    }
                } catch (Exception e) {
                    System.out.println("팔로우 관계 생성 실패: 팔로워=" + follower.getEmail() + ", 팔로잉=" + following.getEmail() + " - 오류: " + e.getMessage());
                }
            }
        }
    }
    private User getRandomUser(List<User> users, User excludeUser) {
        User randomUser;
        do {
            randomUser = users.get(random.nextInt(users.size()));
        } while (randomUser.equals(excludeUser));
        return randomUser;
    }


}
