package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.dto.notification.UUID;
import com.part3.team07.sb01deokhugamteam07.entity.Like;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, UUID> {

  // 파워 유저에서 이용되는 메서드, 특정 사용자가 작성한 댓글을 날짜 범위내에서 조회하여 개수 반환
  long countByUserIdAndCreatedAtBetween(
      java.util.UUID userId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime
  );

  Optional<Like> findByReviewIdAndUserId(java.util.UUID reviewId, java.util.UUID userId);
}
