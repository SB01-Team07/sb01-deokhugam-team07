package com.part3.team07.sb01deokhugamteam07.repository;

// import com.part3.team07.sb01deokhugamteam07.dto.notification.UUID; -> 0420 원길님께 임시조치
import com.part3.team07.sb01deokhugamteam07.entity.User;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  List<User> findByIsDeletedFalse();
  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
