package com.part3.team07.sb01deokhugamteam07.controller;

import com.part3.team07.sb01deokhugamteam07.dto.notification.response.CursorPageResponseNotificationDto;
import com.part3.team07.sb01deokhugamteam07.service.NotificationService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  /**
   * 알림 목록을 커서 기반 페이지네이션으로 조회합니다.
   **/
  @GetMapping
  public ResponseEntity<CursorPageResponseNotificationDto> find(
      @RequestParam @NotNull(message = "사용자 id는 필수입니다.") UUID userId,
      @RequestParam(required = false, defaultValue = "desc")  @Pattern(regexp = "(?i)ASC|DESC", message = "direction은 ASC 또는 DESC만 가능합니다.") String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) String after,
      @RequestParam(required = false, defaultValue = "20") @Min(1) int limit
  ) {
    log.info("알림 조회 요청: userId={}, direction={}, cursor={}, after={}, limit={}",
        userId, direction, cursor, after, limit);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(notificationService.find(userId, direction, cursor, after, limit));
  }
}
