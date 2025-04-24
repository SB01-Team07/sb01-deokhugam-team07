package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.comment.response.CursorPageResponseCommentDto;
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
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
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
    commentRepository.delete(comment);
    log.info("hardDelete comment complete");
  }

  @Transactional(readOnly = true)
  public CursorPageResponseCommentDto findCommentsByReviewId(UUID reviewId, LocalDateTime cursorCreatedAt, int size) {
    //리뷰 존재 여부 확인
    Review review = findReview(reviewId);

    //다음 페이지 존재 여부 판단 위해 size+1로 요청
    Pageable pageable = PageRequest.of(0, size+1);

    List<Comment> comments;
    if (cursorCreatedAt == null) {
      // 커서 없을 때: 최신 댓글부터 size+1개 조회
      comments = commentRepository.findByReviewAndDeletedFalseOrderByCreatedAtDesc(review, pageable);
    } else {
      // 커서 있을 때: cursor 이전 댓글 조회
      comments = commentRepository.findByReviewAndDeletedFalseAndCreatedAtLessThanOrderByCreatedAtDesc(
          review, cursorCreatedAt, pageable
      );
    }

    // 다음 페이지가 있다면 하나 빼서 자름
    boolean hasNext = comments.size() > size;
    if (hasNext) {
      comments = comments.subList(0, size);
    }

    List<CommentDto> content = comments.stream()
        .map(commentMapper::toDto)
        .toList();

    LocalDateTime nextCursor = hasNext ? comments.get(comments.size() - 1).getCreatedAt() : null;

    return new CursorPageResponseCommentDto(
        content,
        nextCursor,          // 다음 요청을 위한 커서
        nextCursor,          // nextAfter도 동일하게 세팅
        size,
        content.size(),
        hasNext
    );
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

}
