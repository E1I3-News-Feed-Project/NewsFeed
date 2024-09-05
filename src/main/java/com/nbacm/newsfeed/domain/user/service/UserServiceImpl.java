package com.nbacm.newsfeed.domain.user.service;

import com.nbacm.newsfeed.domain.user.common.utils.JwtUtils;
import com.nbacm.newsfeed.domain.user.common.utils.PasswordUtils;
import com.nbacm.newsfeed.domain.user.dto.request.UserLoginRequestDto;
import com.nbacm.newsfeed.domain.user.dto.request.UserRequestDto;
import com.nbacm.newsfeed.domain.user.dto.response.MyPageUserResponseDto;
import com.nbacm.newsfeed.domain.user.dto.response.UserResponseDto;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.exception.*;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${profile.image.base.dir}")
    private String baseDirectory;


    @Override
    @Transactional
    public UserResponseDto signup(UserRequestDto userRequestDto, MultipartFile profileImage) throws IOException {

        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("사용중인 이메일 입니다.");
        }
        if(userRepository.findByNickname(userRequestDto.getNickname()).isPresent()){
            throw new NickNameAlreadyExistsException("사용중인 닉네임 입니다");
        }
        // 프로필 이미지가 있을 경우 먼저 저장하여 이미지 경로를 얻음
        String imagePath = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            imagePath = saveProfileImage(profileImage, userRequestDto.getEmail()); // 사용자 식별자로 이메일 사용
        }
        // User 객체를 한 번에 생성하여 저장
        User user = User.builder()
                .email(userRequestDto.getEmail())
                .password(PasswordUtils.hashPassword(userRequestDto.getPassword()))
                .nickname(userRequestDto.getNickname())
                .profileImage(imagePath) // 저장된 프로필 이미지 경로를 포함
                .build();

        user = userRepository.save(user);

        return UserResponseDto.from(user);

    }

    @Override
    public String login(UserLoginRequestDto userLoginRequestDto) {
        User user = userRepository.findByEmailOrElseThrow(userLoginRequestDto.getEmail());
        if (!PasswordUtils.checkPassword(userLoginRequestDto.getPassword(), user.getPassword())) {
            throw new NotMatchException("잘못된 비밀번호 입니다.");
        }
        if(user.isDeleted()){
            throw new AlreadyDeletedException("탈퇴한 계정입니다");
        }
        String accessToken = jwtUtils.generateToken(user.getEmail());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());
        // Redis에 RefreshToken 저장
        redisTemplate.opsForValue().set(
                "RT:" + user.getEmail(),
                refreshToken,
                jwtUtils.getRefreshTokenExpirationTime(),
                TimeUnit.MILLISECONDS
        );
        return accessToken;
    }

    @Override
    public String logout(String accessToken) {
        String email = jwtUtils.getUserEmailFromToken(accessToken);
        // RefreshToken 삭제
        redisTemplate.delete("RT:" + email);
        return null;
    }
    @Override
    @Transactional
    public UserResponseDto updateUser(String email,UserRequestDto userRequestDto,MultipartFile profileImage) throws IOException {
        User user = userRepository.findByEmailOrElseThrow(email);

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
    public MyPageUserResponseDto getUser(String email) {
        User user = userRepository.findByEmailOrElseThrow(email);
        String imageUrl = null;
        if(user.getProfileImage() != null && !user.getProfileImage().isEmpty()){
            Path filePath = Paths.get(baseDirectory, user.getProfileImage());
            imageUrl = baseDirectory + "/"+email+filePath.getFileName().toString();
        }
        return MyPageUserResponseDto.from(user, imageUrl);
    }

    @Override
    @Transactional
    public void deleteAccount(String email, String password) {
        User user = userRepository.findByEmailOrElseThrow(email);
        if(!PasswordUtils.checkPassword(password, user.getPassword())){
            throw new InvalidPasswordException("올바르지 않은 비밀번호 입니다.");
        }
        user.deleteAccount();
        userRepository.save(user);
        redisTemplate.delete("RT:" + user.getEmail());
    }

    @Override
    public String saveProfileImage(MultipartFile profileImage, String email) throws IOException {
        // 사용자별 디렉토리 경로 생성
        String userDirectory = baseDirectory + "/" + email;
        Path userPath = Paths.get(userDirectory);

        // 디렉토리가 존재하지 않으면 생성
        if (!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }

        // 파일 형식 검증 (예: JPG, JPEG, PNG만 허용)
        String fileExtension = StringUtils.getFilenameExtension(profileImage.getOriginalFilename());
        if (!Arrays.asList("jpg", "jpeg", "png").contains(fileExtension.toLowerCase())) {
            throw new IOException("Invalid file type. Only JPG, JPEG, and PNG are allowed.");
        }

        // 파일 크기 제한 (5MB 이하로 제한)
        long maxFileSize = 5 * 1024 * 1024;
        if (profileImage.getSize() > maxFileSize) {
            throw new IOException("File size exceeds the maximum limit of 5MB.");
        }

        // 원본 파일 이름을 사용하지 않고, 이메일 또는 다른 값을 기반으로 파일 이름 설정
        String newFileName = email + "_profile_image_" + UUID.randomUUID() + "." + fileExtension;
        Path targetLocation = userPath.resolve(newFileName);

        // 파일 저장
        Files.copy(profileImage.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 저장된 파일 경로를 문자열로 반환
        return targetLocation.toString();
    }

    @Override
    public Resource loadProfileImage(String email) throws IOException {
        User user = userRepository.findByEmailOrElseThrow(email);
        String imagePath = user.getProfileImage();
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
    public void deleteExistingProfileImage(User user) {
        String existingProfileImage = user.getProfileImage();
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

