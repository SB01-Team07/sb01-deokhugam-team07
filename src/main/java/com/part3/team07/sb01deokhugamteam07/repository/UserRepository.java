package com.part3.team07.sb01deokhugamteam07.repository;


import com.part3.team07.sb01deokhugamteam07.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);

  boolean existsById(UUID id);

  Optional<User> findByEmail(String email);

  @Query(value = "SELECT DISTINCT u.* FROM users u " +
      "LEFT JOIN reviews r ON u.id = r.user_id AND r.is_deleted = false " +
      "AND r.created_at BETWEEN :startDateTime AND :endDateTime " +
      "LEFT JOIN comments c ON u.id = c.user_id AND c.is_deleted = false " +
      "AND c.created_at BETWEEN :startDateTime AND :endDateTime " +
      "LEFT JOIN likes l ON u.id = l.user_id AND l.is_deleted = false " +
      "AND l.created_at BETWEEN :startDateTime AND :endDateTime " +
      "WHERE u.is_deleted = false " +
      "AND (r.id IS NOT NULL OR c.id IS NOT NULL OR l.id IS NOT NULL)" +
      "ORDER BY u.created_at ASC",
      nativeQuery = true)
  List<User> findPowerUserInPeriod(
      @Param("startDateTime") LocalDateTime startDateTime,
      @Param("endDateTime") LocalDateTime endDateTime
  );
}
