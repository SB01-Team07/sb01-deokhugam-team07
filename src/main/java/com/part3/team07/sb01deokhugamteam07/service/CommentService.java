package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.exception.user.UserNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.comment.CommentNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.comment.CommentUnauthorizedException;
import com.part3.team07.sb01deokhugamteam07.mapper.CommentMapper;
import com.part3.team07.sb01deokhugamteam07.repository.CommentRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;
  private final CommentMapper commentMapper;

  @Transactional
  public CommentDto create(CommentCreateRequest createRequest) {
    log.debug("create comment {}", createRequest);
    User user = findUser(createRequest.userId());
    Review review = findReview(createRequest.reviewId());

    Comment comment = Comment.builder()
        .user(user)
        .review(review)
        .content(createRequest.content())
        .build();

    commentRepository.save(comment);
    log.info("create comment complete: id={}, comment={}", comment.getId(), comment.getContent());
    increaseCommentCount(review.getId()); //review commentCount 증가
    return commentMapper.toDto(comment);
  }

  @Transactional
  public CommentDto update(UUID commentId, UUID userId, CommentUpdateRequest updateRequest) {
    log.debug("update comment: commentId = {}, userId = {}, request = {}", commentId, userId,
        updateRequest);
    Comment comment = findComment(commentId);
    User user = findUser(userId);
    validateCommentAuthor(comment, user);
    comment.update(updateRequest.content());
    log.info("update comment complete: id={}, comment={}", comment.getId(), comment.getContent());
    return commentMapper.toDto(comment);
  }

  @Transactional(readOnly = true)
  public CommentDto find(UUID commentId) {
    log.debug("find comment: commentId = {}", commentId);
    Comment comment = findComment(commentId);
    isSoftDeleted(comment);
    log.info("find comment complete: commentId = {}", comment.getId());
    return commentMapper.toDto(comment);
  }

  @Transactional
  public void softDelete(UUID commentId, UUID userId) {
    log.debug("softDelete comment: commentId = {}", commentId);
    Comment comment = findComment(commentId);
    isSoftDeleted(comment);
    User user = findUser(userId);
    validateCommentAuthor(comment, user);
    comment.softDelete();
    decreaseCommentCountOnSoftDelete(comment); //review commentCount 감소 - 댓글 논리 삭제가 실제 일어났다고 보장된 상태
    log.info("softDelete comment complete");
  }

  // 리뷰 존재 검증은 따로 안했습니다.
  @Transactional
  public void softDeleteAllByReview(Review review) {
    log.debug("softDelete all comments by review: review = {}", review);
    List<Comment> comments = commentRepository.findAllByReview(review);
    for (Comment comment : comments) {
      comment.softDelete();
    }
    log.info("softDelete all comments by review complete");
  }

  @Transactional
  public void hardDelete(UUID commentId, UUID userId) {
    log.debug("hardDelete comment: commentId = {}, userId = {}",commentId, userId);
    Comment comment = findComment(commentId);
    User user = findUser(userId);
    validateCommentAuthor(comment, user);
    decreaseCommentCountOnHardDelete(comment); //review commentCount 감소
    commentRepository.delete(comment);
    log.info("hardDelete comment complete");
  }

  private void isSoftDeleted(Comment comment) {
    if (comment.isDeleted()) {
      throw new CommentNotFoundException();
    }
  }

  private Comment findComment(UUID commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException());
  }

  private User findUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  private Review findReview(UUID reviewId) {
    return reviewRepository.findById(reviewId)
        .orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다."));
    //todo 예외 추가 시 변경 예정
  }

  private void validateCommentAuthor(Comment comment, User user) {
    if (!comment.getUser().equals(user)) {
      throw new CommentUnauthorizedException();
    }
  }

  //commentCount 감소 - 논리삭제
  private void decreaseCommentCountOnSoftDelete(Comment comment) {
    log.debug("댓글 논리 삭제로 commentCount 감소: reviewId={}, commentId={}", comment.getReview().getId(), comment.getId());
    reviewRepository.decrementCommentCount(comment.getReview().getId());
  }

  //commentCount 감소 - 물리삭제
  private void decreaseCommentCountOnHardDelete(Comment comment) {
    if (!comment.isDeleted()) {
      log.debug("댓글 물리 삭제로 commentCount 감소: reviewId={}, commentId={}", comment.getReview().getId(), comment.getId());
      reviewRepository.decrementCommentCount(comment.getReview().getId());
    }
  }

  //commentCount 증가
  private void increaseCommentCount(UUID reviewId) {
    log.debug("댓글 생성으로 리뷰 commentCount 증가: reviewId={}", reviewId);
    reviewRepository.incrementCommentCount(reviewId);
  }

}
