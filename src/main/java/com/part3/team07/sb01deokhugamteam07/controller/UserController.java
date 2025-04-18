package com.part3.team07.sb01deokhugamteam07.controller;

import com.part3.team07.sb01deokhugamteam07.dto.user.UserDto;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserLoginRequest;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserRegisterRequest;
import com.part3.team07.sb01deokhugamteam07.dto.user.request.UserUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto join(@RequestBody @Validated UserRegisterRequest request) {
    return userService.register(request);
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public UserDto login(@RequestBody @Validated UserLoginRequest request) {
    return userService.login(request);
  }

  @PostMapping("/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public UserDto modify(@RequestBody @Validated UserUpdateRequest request,
      @PathVariable("userId") UUID userID) {

    return userService.update(userID, request);
  }
}
