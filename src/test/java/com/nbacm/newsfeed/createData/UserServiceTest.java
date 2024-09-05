package com.nbacm.newsfeed.createData;
import com.nbacm.newsfeed.domain.user.dto.request.UserRequestDto;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import com.nbacm.newsfeed.domain.user.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;


    @Test
    void testSignupWith1000Users() throws IOException {
        // 1,000명의 사용자 데이터를 생성하고 저장하는 로직
        for (int i = 0; i < 500; i++) {
            String email = "test" + i + "@gmail.com";
            String nickname = "testUser" + i;
            String password = "KangWook@" + i;

            // 테스트용 UserRequestDto 생성
            UserRequestDto userRequestDto = new UserRequestDto(email, password, nickname);

            // 공갈 이미지 생성 (더미 이미지)
            MockMultipartFile mockImage = new MockMultipartFile(
                    "image",                           // 파일 필드명
                    "testImage" + i + ".jpg",           // 파일명
                    "image/jpeg",                       // 파일 타입
                    "fake-image-content".getBytes()     // 파일 내용 (더미 데이터)
            );

            // signup 메서드 호출 (더미 이미지 포함)
            userService.signup(userRequestDto, mockImage);
        }
    }

}
