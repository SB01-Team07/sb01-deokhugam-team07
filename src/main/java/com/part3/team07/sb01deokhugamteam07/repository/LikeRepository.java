package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Like;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, UUID> {

  // 파워 유저에서 이용되는 메서드, 특정 리뷰에 박힌 좋아요 날짜 범위내에서 조회하여 개수 반환
  long countByUserIdAndCreatedAtBetween(
      UUID userId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime
  );
  // 인기 리뷰에서 이용되는 메서드, 특정 리뷰에 박힌 좋아요 날짜 범위내에서 조회하여 개수 반환
  long countByReviewIdAndCreatedAtBetween(
      UUID userId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime
  );

  Optional<Like> findByReviewIdAndUserId(UUID reviewId, UUID userId);

  List<Like> findAllByReviewId(UUID reviewId);
}
