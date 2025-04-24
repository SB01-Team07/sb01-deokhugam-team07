package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Comment;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
  // 파워 유저에서 이용되는 메서드, 특정 사용자가 작성한 댓글을 날짜 범위내에서 조회하여 개수 반환
  long countByUserIdAndCreatedAtBetweenAndIsDeletedFalse(
      java.util.UUID userId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime);

  // 인기 리뷰에서 이용되는 메서드, 특정 리뷰에 작성된 댓글을 날짜 범위내에서 조회하여 개수 반환
  long countByReviewIdAndCreatedAtBetweenAndIsDeletedFalse(
      UUID reviewId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime);

  List<Comment> findAllByReview(Review review);

  List<Comment> findByReviewAndDeletedFalseOrderByCreatedAtDesc(
      Review review,
      Pageable pageable
  );

  List<Comment> findByReviewAndDeletedFalseAndCreatedAtLessThanOrderByCreatedAtDesc(
      Review review,
      LocalDateTime cursorCreatedAt,
      Pageable pageable
  );


}
