package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.repository.querydsl.ReviewRepositoryCustom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {
  // 파워 유저에서 이용되는 메서드, 특정 사용자가 사용한 리뷰를 날짜 범위내에서 조회
  List<Review> findByUserIdAndCreatedAtBetweenAndIsDeletedFalse(
      UUID userId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime
  );

  List<Review> findAllByBook(Book book);

  List<Review> findAllByUser(User user);

  @Modifying
  @Query("UPDATE Review r SET r.likeCount = r.likeCount + 1 WHERE r.id = :id")
  void incrementLikeCount(@Param("id") UUID reviewId);

  @Modifying
  @Query("UPDATE Review r SET r.likeCount = r.likeCount - 1 WHERE r.id = :id AND r.likeCount > 0")
  void decrementLikeCount(@Param("id") UUID reviewId);

  @Modifying
  @Query("UPDATE Review r SET r.commentCount = r.commentCount + 1 WHERE r.id = :id")
  void incrementCommentCount(@Param("id") UUID reviewId);

  @Modifying
  @Query("UPDATE Review r SET r.commentCount = r.commentCount - 1 WHERE r.id = :id AND r.commentCount > 0")
  void decrementCommentCount(@Param("id") UUID reviewId);

  // 인기 도서에서 이용되는 메서드, 특정 리뷰에 작성된 댓글을 날짜 범위내에서 조회
  List<Review> findByBookIdAndCreatedAtBetweenAndIsDeletedFalse(
      UUID bookId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime);

  List<Review> findByIsDeletedFalseOrderByCreatedAtAsc();

  boolean existsByUserIdAndBookId(UUID userId, UUID bookId);

  Optional<Review> findByIdAndIsDeletedFalse(UUID reviewId);

  @Override
  @EntityGraph(attributePaths = {"book", "user"})
  List<Review> findAllById(Iterable<UUID> ids);
}
