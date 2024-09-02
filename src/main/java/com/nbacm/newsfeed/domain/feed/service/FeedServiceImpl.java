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
                    Image image = new Image(newFilename);
                    image.setFeed(savedFeed);
                    images.add(image);
                }
            }

            if (!images.isEmpty()) {
                imageRepository.saveAll(images);
                savedFeed.setImages(images);
                feedRepository.save(savedFeed);
            }
        }
    }

    @Override
    public Optional<FeedResponseDto> getFeedById(Long feed_id, String email) {
        return feedRepository.findById(feed_id)
                .filter(feed -> feed.getAuthor().getEmail().equals(email)) // 작성자 확인
                .map(this::convertToFeedResponseDto);
    }

    public Page<FeedResponseDto> findFeedsByUser(String email, Pageable pageable) {
        Page<Feed> feeds = feedRepository.findByUserEmailOrderByCreatedAtDesc(email, pageable);
        return feeds.map(this::convertToFeedResponseDto);
    }


    @Override
    @Transactional
    public FeedResponseDto updateFeed(Long feed_id, FeedRequestDto feedRequestDto, List<MultipartFile> images, String email) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotMatchException("사용자를 찾을 수 없습니다."));

        Feed feed = feedRepository.findById(feed_id)
                .orElseThrow(() -> new NotMatchException("게시물이 존재하지 않습니다."));

        if (!feed.getAuthor().getEmail().equals(email)) {
            throw new NotMatchException("작성자만 게시물을 수정할 수 있습니다.");
        }

        feed.setContent(feedRequestDto.getContent());

        if (images != null) {
            // 삭제할 기존 이미지를 처리합니다.
            List<Image> existingImages = new ArrayList<>(feed.getImages());
            feed.getImages().clear();

            // 새로운 이미지를 처리합니다.
            List<Image> newImages = new ArrayList<>();
            for (MultipartFile imageFile : images) {
                if (!imageFile.isEmpty()) {
                    String newFilename = saveImage(imageFile, user.getEmail());
                    Image image = new Image(newFilename);
                    image.setFeed(feed);
                    newImages.add(image);
                }
            }

            // 기존 이미지 삭제
            imageRepository.deleteAll(existingImages);
            // 새로운 이미지를 저장
            if (!newImages.isEmpty()) {
                imageRepository.saveAll(newImages);
                feed.getImages().addAll(newImages);  // 새로운 이미지 추가
            }
        }

        // 엔티티 저장
        feedRepository.save(feed);
        return convertToFeedResponseDto(feed);
    }

    @Override
    @Transactional
    public boolean deleteFeed(Long feed_id, String email) {
        Feed feed = feedRepository.findById(feed_id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        if (!feed.getAuthor().getEmail().equals(email)) {
            throw new IllegalArgumentException("작성자만 게시물을 삭제할 수 있습니다.");
        }

        feedRepository.deleteById(feed_id);
        imageRepository.deleteByFeedId(feed_id);
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

        return fileName;
    }

    private FeedResponseDto convertToFeedResponseDto(Feed feed) {
        List<String> imageNames = feed.getImages().stream()
                .map(Image::getImageName)
                .collect(Collectors.toList());

        return new FeedResponseDto(
                feed.getFeedId(),
                feed.getContent(),
                imageNames,
                feed.getAuthor().getEmail(),
                feed.getLikesCount()
        );
    }
}