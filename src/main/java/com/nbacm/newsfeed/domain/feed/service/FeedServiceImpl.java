package com.nbacm.newsfeed.domain.feed.service;

import com.nbacm.newsfeed.domain.feed.dto.request.FeedRequestDto;
import com.nbacm.newsfeed.domain.feed.dto.response.FeedResponseDto;
import com.nbacm.newsfeed.domain.feed.entity.Feed;
import com.nbacm.newsfeed.domain.feed.entity.Image;
import com.nbacm.newsfeed.domain.feed.repository.FeedRepository;
import com.nbacm.newsfeed.domain.feed.repository.ImageRepository;
import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.exception.NotMatchException;
import com.nbacm.newsfeed.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public void createFeed(FeedRequestDto requestDto) throws IOException {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        Feed feed = new Feed(requestDto.getContent(), user, 0);
        Feed savedFeed = feedRepository.save(feed);

        if (requestDto.getImages() != null) {
            List<Image> images = new ArrayList<>();
            for (MultipartFile imageFile : requestDto.getImages()) {
                if (!imageFile.isEmpty()) {
                    String newFilename = saveImage(imageFile, user.getEmail());
                    Image image = new Image(newFilename, feed);
                    images.add(image);
                }
            }

            if (!images.isEmpty()) {
                imageRepository.saveAll(images);
                savedFeed.updateImages(images);
//                feedRepository.save(savedFeed);
            }
        }
        feedRepository.save(savedFeed);
    }

    @Override
    public Optional<FeedResponseDto> getFeedById(Long feedId, String email) {
        return feedRepository.findById(feedId)
                .filter(feed -> feed.getAuthor().getEmail().equals(email)) // 작성자 확인
                .map(FeedResponseDto::from);
    }

    public Page<FeedResponseDto> findFeedsByUser(String email, Pageable pageable) {
        Page<Feed> feeds = feedRepository.findByUserEmailOrderByCreatedAtDesc(email, pageable);
        return feeds.map(FeedResponseDto::from);
    }


    @Override
    @Transactional
    public FeedResponseDto updateFeed(Long feedId, FeedRequestDto feedRequestDto, List<MultipartFile> images, String email) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotMatchException("사용자를 찾을 수 없습니다."));

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new NotMatchException("게시물이 존재하지 않습니다."));

        if (!feed.getAuthor().getEmail().equals(email)) {
            throw new NotMatchException("작성자만 게시물을 수정할 수 있습니다.");
        }

        // 게시물 내용 업데이트
        feed.updateContent(feedRequestDto.getContent());

        // 이미지 업데이트
        if (images != null) {
            // 기존 이미지 파일 및 데이터베이스 엔티티 삭제
            List<Image> existingImages = new ArrayList<>(feed.getImages());
            feed.getImages().clear();

            for (Image image : existingImages) {
                String imagePath = image.getImageName();
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        Path fileToDeletePath = Paths.get(imagePath);
                        Files.deleteIfExists(fileToDeletePath);
                    } catch (IOException e) {
                        System.err.println("이미지 삭제 중 오류 발생: " + e.getMessage());
                    }
                }
            }

            // 기존 이미지 엔티티 삭제
            imageRepository.deleteAll(existingImages);

            // 새로운 이미지 처리 및 저장
            List<Image> newImages = new ArrayList<>();
            for (MultipartFile imageFile : images) {
                if (!imageFile.isEmpty()) {
                    String newFilename = saveImage(imageFile, user.getEmail());
                    Image image = new Image(newFilename, feed);
                    newImages.add(image);
                }
            }

            if (!newImages.isEmpty()) {
                imageRepository.saveAll(newImages);
                feed.getImages().addAll(newImages);  // 새로운 이미지 추가
            }
        }

        // 게시물 엔티티 저장
        Feed updatedFeed = feedRepository.save(feed);
        return FeedResponseDto.from(updatedFeed);
    }

    @Override
    @Transactional
    public boolean deleteFeed(Long feedId, String email) {
        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        if (!feed.getAuthor().getEmail().equals(email)) {
            throw new IllegalArgumentException("작성자만 게시물을 삭제할 수 있습니다.");
        }

        // 1. 이미지 파일 삭제
        List<Image> images = feed.getImages();
        for (Image image : images) {
            String imagePath = image.getImageName(); // 이미지 파일 경로
            Path fileToDeletePath = Paths.get(imagePath);
            try {
                if (Files.exists(fileToDeletePath)) {
                    Files.delete(fileToDeletePath);
                    System.out.println("파일 삭제 성공: " + fileToDeletePath);
                } else {
                    System.out.println("파일이 존재하지 않음: " + fileToDeletePath);
                }
            } catch (IOException e) {
                System.err.println("이미지 삭제 중 오류 발생: " + e.getMessage());
            }
        }

        feedRepository.deleteById(feedId);
        imageRepository.deleteByFeedId(feedId);
        return true;
    }

    private String saveImage(MultipartFile imageFile, String email) throws IOException {
        String userDirectory = uploadDir + "/" + email;
        Path userPath = Paths.get(userDirectory);

        if (!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(imageFile.getOriginalFilename());
        Path targetLocation = userPath.resolve(fileName);

        Files.copy(imageFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return targetLocation.toString();
    }
}