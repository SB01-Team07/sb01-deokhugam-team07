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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserDto register(UserRegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new DuplicateUserEmailException(request);
    }

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

  public UserDto update(UserUpdateRequest request) {
    return null;
  }
}
