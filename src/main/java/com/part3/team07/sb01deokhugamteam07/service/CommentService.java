package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentCreateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.repository.CommentRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;

  public CommentDto create(CommentCreateRequest createRequest) {
    log.info("createRequest.userId() = {}", createRequest.userId());
    log.info("createRequest.reviewId() = {}", createRequest.reviewId());

    User user = userRepository.findById(createRequest.userId())
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    Review review = reviewRepository.findById(createRequest.reviewId())
        .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

    Comment comment = Comment.builder()
        .user(user)
        .review(review)
        .content(createRequest.content())
        .build();

    commentRepository.save(comment);

    return CommentDto.builder()
        .id(comment.getId())
        .reviewId(review.getId())
        .userId(user.getId())
        .userNickname(user.getNickname())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .build();
  }
}
