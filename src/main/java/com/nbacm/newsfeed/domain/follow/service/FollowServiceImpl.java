package com.nbacm.newsfeed.domain.follow.service;

import com.nbacm.newsfeed.domain.follow.dto.FollowRequestResponse;
import com.nbacm.newsfeed.domain.follow.entity.Follow;
import com.nbacm.newsfeed.domain.follow.entity.FollowRequest;
import com.nbacm.newsfeed.domain.follow.entity.FollowRequestStatus;
import com.nbacm.newsfeed.domain.follow.repository.FollowRepository;
import com.nbacm.newsfeed.domain.follow.repository.FollowRequestRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FollowServiceImpl implements FollowService{

    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public void deleteFollow(Long followingId, HttpServletRequest request) {
        String email = (String) request.getAttribute("AuthenticatedUser");
        User follower = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        User following = userRepository.findById(followingId).orElseThrow(RuntimeException::new);

        followRepository.deleteByFollowerUserIdAndFollowingUserId(follower.getUserId(), following.getUserId());
    }

    @Transactional
    public FollowRequestResponse sendFollowRequest(Long receiverId, HttpServletRequest request) {
        String email = (String) request.getAttribute("AuthenticatedUser");
        User sender = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        User receiver = userRepository.findById(receiverId).orElseThrow(RuntimeException::new);

        if (sender.equals(receiver)) {
            throw new RuntimeException("자신에게 팔로우 신청을 할 수 없습니다.");
        }

        if (followRepository.existsByFollowerUserIdAndFollowingUserId(sender.getUserId(), receiver.getUserId())) {
            throw new RuntimeException("이미 팔로우가 되어있는 사용자입니다.");
        }

        if (followRequestRepository.findBySenderUserIdAndReceiverUserIdAndStatus(sender.getUserId(), receiver.getUserId(), FollowRequestStatus.PENDING).isPresent()) {
            throw new RuntimeException("이미 팔로우 신청을 한 사용자입니다.");
        }

        FollowRequest followRequest = FollowRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .build();

        followRequestRepository.save(followRequest);
        return FollowRequestResponse.from(followRequest);
    }

    @Transactional
    public FollowRequestResponse acceptFollowRequest(Long requestId, HttpServletRequest request) {
        String email = (String) request.getAttribute("AuthenticatedUser");
        User receiver = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        FollowRequest followRequest = followRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("팔로우 신청을 찾을 수 없습니다."));

        if (!followRequest.getReceiver().getUserId().equals(receiver.getUserId())) {
            throw new RuntimeException("요청을 처리할 권한이 없습니다.");
        }

        Follow follow = Follow.builder()
                .follower(followRequest.getSender())
                .following(followRequest.getReceiver())
                .build();

        followRequest.accept();
        followRepository.save(follow);
        return FollowRequestResponse.from(followRequest);
    }

    @Transactional
    public FollowRequestResponse rejectFollowRequest(Long requestId, HttpServletRequest request) {
        String email = (String) request.getAttribute("AuthenticatedUser");
        User receiver = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        FollowRequest followRequest = followRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("팔로우 신청을 찾을 수 없습니다."));

        if (!followRequest.getReceiver().getUserId().equals(receiver.getUserId())) {
            throw new RuntimeException("요청을 처리할 권한이 없습니다.");
        }

        followRequest.reject();

        return FollowRequestResponse.from(followRequest);
    }
}
