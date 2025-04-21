package com.part3.team07.sb01deokhugamteam07.repository;

//import com.part3.team07.sb01deokhugamteam07.dto.notification.UUID;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);
}
