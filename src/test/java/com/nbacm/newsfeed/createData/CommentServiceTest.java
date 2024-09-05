package com.nbacm.newsfeed.createData;

import com.nbacm.newsfeed.domain.comment.dto.request.CommentRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.response.CommentResponseDto;
import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.comment.service.CommentService;
import com.nbacm.newsfeed.domain.comment.service.CommentServiceImpl;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.feed.repository.FeedRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Random;

@SpringBootTest
public class CommentServiceTest {
    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createCommentsForExistingFeeds() {
        Random random = new Random();

        Pageable pageable = PageRequest.of(0, 100); // 한 번에 100개의 피드를 처리
        Page<Feed> feedPage;

        do {
            feedPage = feedRepository.findAll(pageable);

            for (Feed feed : feedPage.getContent()) {
                int commentCount = random.nextInt(5) + 1; // 각 피드당 1~5개의 댓글 생성

                for (int i = 0; i < commentCount; i++) {
                    try {
                        // 랜덤한 사용자 이메일 선택 (test0@gmail.com ~ test499@gmail.com)
                        String randomEmail = "test" + random.nextInt(500) + "@gmail.com";

                        // 사용자 정보 가져오기
                        User user = userRepository.findByEmailOrElseThrow(randomEmail);
                        String nickname = user.getNickname();

                        String commentContent = "테스트 댓글 내용 - 피드 ID: " + feed.getFeedId() + ", 작성자: " + nickname;
                        CommentRequestDto commentRequestDto = new CommentRequestDto(commentContent);

                        // 댓글 생성 (좋아요 카운트는 기본적으로 0으로 시작)
                        CommentResponseDto responseDto = commentService.addComment(feed.getFeedId(), randomEmail, commentRequestDto);

                        System.out.println("댓글 생성 성공: FeedId=" + feed.getFeedId() + ", UserEmail=" + randomEmail + ", Nickname=" + nickname + ", CommentId=" + responseDto.getId());
                    } catch (Exception e) {
                        System.out.println("댓글 생성 실패: FeedId=" + feed.getFeedId() + " - 오류: " + e.getMessage());
                    }
                }
            }

            pageable = feedPage.nextPageable();
        } while (feedPage.hasNext());
    }
}
