package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.repository.CommentRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.util.NoSuchElementException;
import java.util.UUID;
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
    log.debug("create comment {}", createRequest);
    Review review = reviewRepository.findById(createRequest.reviewId())
        .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다.")); // 예외 추가 시 변경 예정

    User user = userRepository.findById(createRequest.userId())
        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다.")); // 예외 추가 시 변경 예정

    Comment comment = Comment.builder()
        .user(user)
        .review(review)
        .content(createRequest.content())
        .build();

    commentRepository.save(comment);

    log.info("create comment complete: id={}, comment={}", comment.getId(), comment.getContent());
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

  public CommentDto update(UUID commentId, UUID userId, CommentUpdateRequest updateRequest){
    log.debug("update comment: commentId = {}, userId = {}, request = {}", commentId, userId, updateRequest);
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다.")); // 예외 추가 시 변경 예정

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다.")); // 예외 추가 시 변경 예정

    if (!comment.getUser().getId().equals(user.getId())){
      throw new IllegalArgumentException("댓글 수정 권한 없음.");
    }

    comment.update(updateRequest.content());
    commentRepository.save(comment);
    return CommentDto.builder()
        .id(comment.getId())
        .reviewId(comment.getReview().getId())
        .userId(user.getId())
        .userNickname(user.getNickname())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .build();
  }

}
