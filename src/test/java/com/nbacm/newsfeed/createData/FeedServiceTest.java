package com.nbacm.newsfeed.createData;

import com.nbacm.newsfeed.domain.feed.dto.request.FeedRequestDto;
import com.nbacm.newsfeed.domain.feed.service.FeedServiceImpl;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class FeedServiceTest {
    @Autowired
    private FeedServiceImpl feedService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createFeedsForExistingUsers() throws IOException {
        for (int i = 0; i < 500; i++) {
            String userEmail = "test" + i + "@gmail.com";

            try {
                User user = userRepository.findByEmailOrElseThrow(userEmail);

                String content = "테스트 피드 내용 - 작성자: " + user.getEmail();
                List<MultipartFile> images = new ArrayList<>();

                for (int j = 0; j < 2; j++) {
                    MockMultipartFile image = new MockMultipartFile(
                            "image",
                            "testImage" + i + "_" + j + ".jpg",
                            "image/jpeg",
                            ("fake-image-content-" + i + "-" + j).getBytes()
                    );
                    images.add(image);
                }

                FeedRequestDto feedRequestDto = new FeedRequestDto(content, images, user.getEmail());

                feedService.createFeed(feedRequestDto);
                System.out.println("피드 생성 성공: " + user.getEmail());
            } catch (Exception e) {
                System.out.println("피드 생성 실패 또는 사용자가 존재하지 않음: " + userEmail + " - 오류: " + e.getMessage());
            }
        }
    }
}
