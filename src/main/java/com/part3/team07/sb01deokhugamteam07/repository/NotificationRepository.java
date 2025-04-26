package com.part3.team07.sb01deokhugamteam07.repository;

import com.part3.team07.sb01deokhugamteam07.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID>{

  long countAllByUserId(UUID userId);

  List<Notification> findAllByUserId(UUID userId);

  void deleteAllByConfirmedTrueAndCreatedAtBefore(LocalDateTime localDateTime);
}
