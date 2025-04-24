package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Book;
import com.part3.team07.sb01deokhugamteam07.entity.Review;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.part3.team07.sb01deokhugamteam07.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
  // 파워 유저에서 이용되는 메서드, 특정 사용자가 사용한 리뷰를 날짜 범위내에서 조회
  List<Review> findByUserIdAndCreatedAtBetweenAndIsDeletedFalse(
      UUID userId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime
  );

  boolean existsByUserIdAndBookId(UUID userId, UUID bookId);

  List<Review> findAllByBook(Book book);

  List<Review> findAllByUser(User user);
  // 인기 도서에서 이용되는 메서드, 특정 리뷰에 작성된 댓글을 날짜 범위내에서 조회
  List<Review> findByBookIdAndCreatedAtBetweenAndIsDeletedFalse(
      UUID bookId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime);

  List<Review> findByIsDeletedFalseOrderByCreatedAtAsc();
}
