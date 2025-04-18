package com.part3.team07.sb01deokhugamteam07.service;

import com.part3.team07.sb01deokhugamteam07.dto.user.UserDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserLoginRequest;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserRegisterRequest;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.entity.User;
import com.part3.team07.sb01deokhugamteam07.exception.user.DuplicateUserEmailException;
import com.part3.team07.sb01deokhugamteam07.exception.user.IllegalUserPasswordException;
import com.part3.team07.sb01deokhugamteam07.exception.user.UserNotFoundException;
import com.part3.team07.sb01deokhugamteam07.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserDto register(UserRegisterRequest request) {
    validateEmail(request);
    String encodedPassword = passwordEncoder.encode(request.password());

    User user = User.builder()
        .email(request.email())
        .nickname(request.nickname())
        .password(encodedPassword)
        .build();

    User savedUser = userRepository.save(user);

    return UserDto.builder()
        .id(savedUser.getId())
        .nickname(savedUser.getNickname())
        .email(savedUser.getEmail())
        .createdAt(savedUser.getCreatedAt())
        .build();
  }

  public UserDto login(UserLoginRequest request) {
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new UserNotFoundException(request));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new IllegalUserPasswordException(request);
    }

    return UserDto.builder()
        .id(user.getId())
        .nickname(user.getNickname())
        .email(user.getEmail())
        .createdAt(user.getCreatedAt())
        .build();
  }

  public UserDto update(UUID userId, UserUpdateRequest request) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(userId);
    }

    User findUser = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    findUser.changeNickname(request.nickname());

    return UserDto.builder()
        .id(findUser.getId())
        .email(findUser.getEmail())
        .createdAt(findUser.getCreatedAt())
        .nickname(findUser.getNickname())
        .build();
  }

  private void validateEmail(UserRegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new DuplicateUserEmailException(request);
    }
  }

  public UserDto find(UUID userId) {
    return null;
  }
}
