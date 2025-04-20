package com.part3.team07.sb01deokhugamteam07.repository;

// import com.part3.team07.sb01deokhugamteam07.dto.notification.UUID; -> 0420 원길님께 임시조치
import com.part3.team07.sb01deokhugamteam07.entity.User;
import java.util.UUID; // 나중에 위에 주석 풀고 이거 지우고 pull 임의 설정
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);
}
