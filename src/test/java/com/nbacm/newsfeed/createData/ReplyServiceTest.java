package com.nbacm.newsfeed.createData;

import com.nbacm.newsfeed.domain.comment.dto.request.ReplyRequestDto;
import com.nbacm.newsfeed.domain.comment.dto.response.ReplyResponseDto;
import com.nbacm.newsfeed.domain.comment.entity.Comment;
import com.nbacm.newsfeed.domain.comment.repository.CommentRepository;
import com.nbacm.newsfeed.domain.comment.service.ReplyService;
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
public class ReplyServiceTest {
    @Autowired
    private ReplyService replyService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createReplyCommentsForExistingComments() {
        Random random = new Random();

        Pageable pageable = PageRequest.of(0, 100); // 한 번에 100개의 댓글을 처리
        Page<Comment> commentPage;

        do {
            commentPage = commentRepository.findAll(pageable);

            for (Comment comment : commentPage.getContent()) {
                int replyCount = random.nextInt(3) + 1; // 각 댓글당 1~3개의 대댓글 생성

                for (int i = 0; i < replyCount; i++) {
                    try {
                        // 랜덤한 사용자 이메일 선택 (test0@gmail.com ~ test499@gmail.com)
                        String randomEmail = "test" + random.nextInt(500) + "@gmail.com";

                        // 사용자 정보 가져오기
                        User user = userRepository.findByEmailOrElseThrow(randomEmail);
                        String nickname = user.getNickname();

                        String replyContent = "테스트 대댓글 내용 - 댓글 ID: " + comment.getCommentId() + ", 작성자: " + nickname;

                        // ReplyRequestDto 생성 (생성자를 통해 초기화)
                        ReplyRequestDto replyRequestDto = new ReplyRequestDto(replyContent, nickname);

                        // 대댓글 생성
                        ReplyResponseDto responseDto = replyService.ReplyComments(comment.getCommentId(), randomEmail, replyRequestDto);

                        System.out.println("대댓글 생성 성공: CommentId=" + comment.getCommentId() + ", UserEmail=" + randomEmail + ", Nickname=" + nickname + ", ReplyId=" + responseDto.getId());
                    } catch (Exception e) {
                        System.out.println("대댓글 생성 실패: CommentId=" + comment.getCommentId() + " - 오류: " + e.getMessage());
                    }
                }
            }

            pageable = commentPage.nextPageable();
        } while (commentPage.hasNext());
    }
}
