package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationType;
import com.part3.team07.sb01deokhugamteam07.dto.comment.response.CursorPageResponseCommentDto;
import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.exception.comment.InvalidCommentQueryException;
import com.part3.team07.sb01deokhugamteam07.exception.review.ReviewNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.user.UserNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.comment.CommentNotFoundException;
import com.part3.team07.sb01deokhugamteam07.exception.comment.CommentUnauthorizedException;
import com.part3.team07.sb01deokhugamteam07.mapper.CommentMapper;
import com.part3.team07.sb01deokhugamteam07.repository.CommentRepository;
import com.part3.team07.sb01deokhugamteam07.repository.ReviewRepository;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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

  private final NotificationService notificationService;

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

    // 알림 생성
    log.info("댓글 알림 생성");
    NotificationCreateRequest notificationRequest = NotificationCreateRequest.builder()
        .type(NotificationType.REVIEW_COMMENTED)
        .senderId(createRequest.userId())
        .reviewId(createRequest.reviewId())
        .build();
    notificationService.create(notificationRequest);

    commentRepository.save(comment);
    log.info("create comment complete: id={}, comment={}", comment.getId(), comment.getContent());
    increaseCommentCount(review); //review commentCount 증가
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
    log.info("find comment complete: commentId = {}", comment.getId());
    return commentMapper.toDto(comment);
  }

  @Transactional
  public void softDelete(UUID commentId, UUID userId) {
    log.debug("softDelete comment: commentId = {}", commentId);
    Comment comment = findComment(commentId);
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
    List<Comment> comments = commentRepository.findAllByReviewAndIsDeletedFalse(review);
    for (Comment comment : comments) {
      comment.softDelete();
    }
    log.info("softDelete all comments by review complete");
  }

  @Transactional
  public void hardDelete(UUID commentId, UUID userId) {
    log.debug("hardDelete comment: commentId = {}, userId = {}", commentId, userId);
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(CommentNotFoundException::new);
    User user = findUser(userId);
    validateCommentAuthor(comment, user);
    decreaseCommentCountOnHardDelete(comment); //review commentCount 감소
    commentRepository.delete(comment);
    log.info("hardDelete comment complete");
  }

  @Transactional(readOnly = true)
  public CursorPageResponseCommentDto findCommentsByReviewId(
      UUID reviewId,
      String direction,
      String cursor,
      LocalDateTime after,
      int limit
  ) {
    log.debug("find comment list: reviewId = {}", reviewId);
    //커서, 정렬방향 검증
    if (!"ASC".equalsIgnoreCase(direction) && !"DESC".equalsIgnoreCase(direction)) {
      throw InvalidCommentQueryException.direction();
    }
    if (cursor != null && !cursor.isBlank()) {
      try {
        LocalDateTime.parse(cursor);
      } catch (DateTimeParseException e) {
        throw InvalidCommentQueryException.cursor();
      }
    }

    //리뷰 존재 여부 확인
    Review review = findReview(reviewId);

    //기본 정렬 조건 생성시간으로 지정
    String sortBy = "createdAt";

    //댓글 조회 (limit+1 개 조회)
    List<Comment> comments = commentRepository.findCommentByCursor(
        review,
        direction,
        cursor,
        after,
        limit,
        sortBy
    );

    //다음 페이지 판단
    boolean hasNext = comments.size() > limit;
    if (hasNext) {
      comments = comments.subList(0, limit);
    }

    //댓글 DTO 변환
    List<CommentDto> content = comments.stream()
        .map(commentMapper::toDto)
        .toList();

    //다음 커서 생성
    String nextCursor = null;
    LocalDateTime nextAfter = null;

    if (hasNext) {
      Comment last = comments.get(comments.size() - 1);
      // 기본이 createdAt 기준이라고 설정
      nextCursor = last.getCreatedAt().toString();
      nextAfter = last.getCreatedAt();
    }

    long totalElements = commentRepository.countByReview(review);

    log.info("find comment list: size = {}", totalElements);
    return new CursorPageResponseCommentDto(
        content,
        nextCursor,
        nextAfter,
        limit,
        totalElements,
        hasNext
    );
  }

  private Comment findComment(UUID commentId) {
    return commentRepository.findByIdAndIsDeletedFalse(commentId)
        .orElseThrow(CommentNotFoundException::new);
  }

  private User findUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException());
  }

  private Review findReview(UUID reviewId) {
    return reviewRepository.findById(reviewId)
        .orElseThrow(ReviewNotFoundException::new);
  }

  private void validateCommentAuthor(Comment comment, User user) {
    if (!comment.getUser().equals(user)) {
      throw new CommentUnauthorizedException();
    }
  }

  //commentCount 감소 - 논리삭제
  private void decreaseCommentCountOnSoftDelete(Comment comment) {
    log.debug("댓글 논리 삭제로 commentCount 감소: reviewId={}, commentId={}", comment.getReview().getId(), comment.getId());
    comment.getReview().decreaseCommentCount();
    //reviewRepository.decrementCommentCount(comment.getReview().getId());
  }

  //commentCount 감소 - 물리삭제
  private void decreaseCommentCountOnHardDelete(Comment comment) {
    if (!comment.isDeleted()) {
      log.debug("댓글 물리 삭제로 commentCount 감소: reviewId={}, commentId={}", comment.getReview().getId(), comment.getId());
      comment.getReview().decreaseCommentCount();
      //reviewRepository.decrementCommentCount(comment.getReview().getId());
    }
  }

  //commentCount 증가
  private void increaseCommentCount(Review review) {
    log.debug("댓글 생성으로 리뷰 commentCount 증가: reviewId={}", review.getId());
    review.increaseCommentCount();
  }
}
