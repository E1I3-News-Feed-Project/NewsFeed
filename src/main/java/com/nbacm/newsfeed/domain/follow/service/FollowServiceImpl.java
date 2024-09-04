package com.nbacm.newsfeed.domain.follow.service;

import com.nbacm.newsfeed.domain.follow.dto.FollowRequestResponse;
import com.nbacm.newsfeed.domain.follow.entity.Follow;
import com.nbacm.newsfeed.domain.follow.entity.FollowRequest;
import com.nbacm.newsfeed.domain.follow.entity.FollowRequestStatus;
import com.nbacm.newsfeed.domain.follow.exception.*;
import com.nbacm.newsfeed.domain.follow.repository.FollowRepository;
import com.nbacm.newsfeed.domain.follow.repository.FollowRequestRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
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
    @Override
    public void deleteFollow(Long followingId, String email) {
        User follower = userRepository.findByEmailOrElseThrow(email);
        User following = userRepository.findByIdOrElseThrow(followingId);

        followRepository.deleteByFollowerUserIdAndFollowingUserId(follower.getUserId(), following.getUserId());
    }

    @Transactional
    @Override
    public FollowRequestResponse sendFollowRequest(Long receiverId, String email) {
        User sender = userRepository.findByEmailOrElseThrow(email);
        User receiver = userRepository.findByIdOrElseThrow(receiverId);

        if (sender.equals(receiver)) {
            throw new SelfFollowException();
        }

        if (followRepository.existsByFollowerUserIdAndFollowingUserId(sender.getUserId(), receiver.getUserId())) {
            throw new AlreadyFollowedException();
        }

        if (followRequestRepository.findBySenderUserIdAndReceiverUserIdAndStatus(sender.getUserId(), receiver.getUserId(), FollowRequestStatus.PENDING).isPresent()) {
            throw new DuplicateFollowRequestException();
        }

        FollowRequest followRequest = FollowRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .build();

        followRequestRepository.save(followRequest);
        return FollowRequestResponse.from(followRequest);
    }

    @Transactional
    @Override
    public FollowRequestResponse acceptFollowRequest(Long requestId, String email) {
        User receiver = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        FollowRequest followRequest = followRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("팔로우 신청을 찾을 수 없습니다."));

        if (!followRequest.getReceiver().getUserId().equals(receiver.getUserId())) {
            throw new UnauthorizedFollowRequestException();
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
    @Override
    public FollowRequestResponse rejectFollowRequest(Long requestId, String email) {
        User receiver = userRepository.findByEmailOrElseThrow(email);
        FollowRequest followRequest = followRequestRepository.findById(requestId).orElseThrow(FollowRequestNotFoundException::new);

        if (!followRequest.getReceiver().getUserId().equals(receiver.getUserId())) {
            throw new UnauthorizedFollowRequestException();
        }

        followRequest.reject();

        return FollowRequestResponse.from(followRequest);
    }

}
