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

  /**
  * @methodName : register
  * @date : 2025. 4. 17. 11:43
  * @author : wongil
  * @Description: 회원가입
  **/
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

  /**
  * @methodName : login
  * @date : 2025. 4. 18. 13:43
  * @author : wongil
  * @Description: 유저 로그인
  **/
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

  /**
  * @methodName : update
  * @date : 2025. 4. 18. 14:43
  * @author : wongil
  * @Description: 유저 수정
  **/
  public UserDto update(UUID userId, UserUpdateRequest request) {
    isExistsUser(userId);

    User findUser = findUser(userId);

    findUser.changeNickname(request.nickname());

    return UserDto.builder()
        .id(findUser.getId())
        .email(findUser.getEmail())
        .createdAt(findUser.getCreatedAt())
        .nickname(findUser.getNickname())
        .build();
  }

  /**
  * @methodName : find
  * @date : 2025. 4. 18. 16:43
  * @author : wongil
  * @Description: 유저 조회
  **/
  public UserDto find(UUID userId) {
    validateUserId(userId);

    User findUser = findUser(userId);
    isDeleted(userId, findUser);

    return UserDto.builder()
        .id(findUser.getId())
        .email(findUser.getEmail())
        .createdAt(findUser.getCreatedAt())
        .nickname(findUser.getNickname())
        .build();
  }

  /**
  * @methodName : logicalDelete
  * @date : 2025. 4. 18. 16:43
  * @author : wongil
  * @Description: 유저 삭제
  **/
  public void logicalDelete(UUID userId) {
    validateUserId(userId);
    isExistsUser(userId);

    User user = findUser(userId);
    user.logiDelete();
  }

  private void isDeleted(UUID userId, User findUser) {
    if (findUser.isDeleted()) {
      throw new UserNotFoundException(userId);
    }
  }

  private void validateUserId(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException();
    }
  }

  private User findUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  private void validateEmail(UserRegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new DuplicateUserEmailException(request);
    }
  }

  private void isExistsUser(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(userId);
    }
  }
}
