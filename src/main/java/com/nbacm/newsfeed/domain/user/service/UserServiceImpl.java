package com.nbacm.newsfeed.domain.user.service;

import com.nbacm.newsfeed.domain.user.common.utils.JwtUtils;
import com.nbacm.newsfeed.domain.user.common.utils.PasswordUtils;
import com.nbacm.newsfeed.domain.user.dto.request.UserLoginRequestDto;
import com.nbacm.newsfeed.domain.user.dto.request.UserRequestDto;
import com.nbacm.newsfeed.domain.user.dto.response.UserResponseDto;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.exception.NotMatchException;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Value("${profile.image.base.dir}")
    private String baseDirectory;


    @Override
    @Transactional
    public UserResponseDto signUp(UserRequestDto userRequestDto, MultipartFile profile_image) throws IOException {
        String password = PasswordUtils.hashPassword(userRequestDto.getPassword());

        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("사용중인 이메일 입니다.");
        }
        // 프로필 이미지가 있을 경우 먼저 저장하여 이미지 경로를 얻음
        String imagePath = null;
        if (profile_image != null && !profile_image.isEmpty()) {
            imagePath = saveProfileImage(profile_image, userRequestDto.getEmail()); // 사용자 식별자로 이메일 사용
        }
        // User 객체를 한 번에 생성하여 저장
        User user = User.builder()
                .email(userRequestDto.getEmail())
                .password(PasswordUtils.hashPassword(userRequestDto.getPassword()))
                .nickName(userRequestDto.getNickname())
                .profile_image(imagePath) // 저장된 프로필 이미지 경로를 포함
                .build();

        // DB에 한 번의 insert 쿼리로 저장
        user = userRepository.save(user);

        return UserResponseDto.from(user);

    }

    @Override
    public String login(UserLoginRequestDto userLoginRequestDto) {
        User user = userRepository.findByEmail(userLoginRequestDto.getEmail())
                .orElseThrow(() -> new NotMatchException("이메일이 일치 하지 않습니다."));
        if (!PasswordUtils.checkPassword(userLoginRequestDto.getPassword(), user.getPassword())) {
            throw new NotMatchException("잘못된 비밀번호 입니다.");
        }
        return jwtUtils.generateToken(user.getEmail());
    }

    @Override
    public String saveProfileImage(MultipartFile profile_image, String email) throws IOException {
        // 사용자별 디렉토리 경로 생성 (ID 사용)
        String userDirectory = baseDirectory + "/" + email;
        Path userPath = Paths.get(userDirectory);

        // 디렉토리가 존재하지 않으면 생성
        if (!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }

        // 고유한 파일 이름 생성
        String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(profile_image.getOriginalFilename());
        Path targetLocation = userPath.resolve(fileName);

        // 파일 저장
        Files.copy(profile_image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 저장된 파일 경로를 문자열로 반환
        return targetLocation.toString();
    }

    @Override
    public Resource loadProfileImage(String email) throws IOException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotMatchException("올바른 접근이 아닌 계정입니다."));
        String imagePath = user.getProfile_image();
        if (imagePath == null || imagePath.isEmpty()) {
            throw new NoSuchFileException("이미지를 찾을수 없습니다.");
        }
        Path filePath = Paths.get(imagePath);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new NoSuchFileException("파일을 읽을수 없습니다");
        }
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(String email,UserRequestDto userRequestDto,MultipartFile profileImage) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotMatchException("사용자를 찾을 수 없습니다."));

        String newPassword = null;
        if (userRequestDto.getPassword() != null) {
            newPassword = PasswordUtils.hashPassword(userRequestDto.getPassword());
        }

        String newProfileImagePath = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            deleteExistingProfileImage(user);
            newProfileImagePath = saveProfileImage(profileImage, user.getEmail());
        }

        user.update(newPassword, userRequestDto.getNickname(), newProfileImagePath);
        userRepository.save(user);

        return UserResponseDto.from(user);
    }


    @Override
    public void deleteExistingProfileImage(User user) {
        String existingProfileImage = user.getProfile_image();
        if (existingProfileImage != null && !existingProfileImage.isEmpty()) {
            try {
                Path fileToDeletePath = Paths.get(existingProfileImage);
                Files.deleteIfExists(fileToDeletePath);
            } catch (IOException e) {
                // 로그를 남기고 예외를 던지지 않음 (파일이 이미 없을 수 있음)
                System.err.println("기존 프로필 이미지 삭제 중 오류 발생: " + e.getMessage());
            }
        }
    }
}

