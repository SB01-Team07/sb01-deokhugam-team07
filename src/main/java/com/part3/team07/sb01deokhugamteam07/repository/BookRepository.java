package com.part3.team07.sb01deokhugamteam07.repository;


import com.part3.team07.sb01deokhugamteam07.entity.Book;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, UUID>, BookRepositoryCustom {

  boolean existsByIsbn(String isbn);

  @Query(value = "SELECT DISTINCT b.* FROM books b " +
      "JOIN reviews r ON r.book_id = b.id " +
      "WHERE b.is_deleted = false " +
      "AND r.is_deleted = false " +
      "AND r.created_at BETWEEN :startDateTime AND :endDateTime " +
      "ORDER BY b.created_at ASC", nativeQuery = true)
  List<Book> findByBookWithReviewsInPeriod(
      @Param("startDateTime") LocalDateTime startDateTime,
      @Param("endDateTime") LocalDateTime endDateTime
  );

}
