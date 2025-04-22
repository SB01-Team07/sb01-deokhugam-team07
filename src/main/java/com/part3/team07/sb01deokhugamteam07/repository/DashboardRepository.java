package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.dto.user.UserMetricsDTO;
import com.part3.team07.sb01deokhugamteam07.entity.Dashboard;
import com.part3.team07.sb01deokhugamteam07.entity.KeyType;
import com.part3.team07.sb01deokhugamteam07.entity.Period;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {
  // 유저의 REVIEW_SCORE_SUM, LIKE_COUNT, COMMENT_COUNT 값을 들고오는 메서드
  @Query(value = "SELECT " +
      "\"key\" AS user_id, " +
      "MAX(CASE WHEN value_type = 'REVIEW_SCORE_SUM' THEN \"value\" END) AS review_score_sum, " +
      "MAX(CASE WHEN value_type = 'LIKE_COUNT' THEN \"value\" END) AS like_count, " +
      "MAX(CASE WHEN value_type = 'COMMENT_COUNT' THEN \"value\" END) AS comment_count " +
      "FROM dashboards " +
      "WHERE key_type = 'USER' " +
      "AND value_type IN ('REVIEW_SCORE_SUM', 'LIKE_COUNT', 'COMMENT_COUNT') " +
      "AND period = :period " +
      "GROUP BY \"key\"", nativeQuery = true)
  List<UserMetricsDTO> getUserMetrics(@Param("period") Period period);

  long countByKeyTypeAndPeriod(KeyType keyType, Period period);
}