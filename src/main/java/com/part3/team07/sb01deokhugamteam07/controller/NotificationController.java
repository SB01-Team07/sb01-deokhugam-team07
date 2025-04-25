package com.part3.team07.sb01deokhugamteam07.controller;

import com.part3.team07.sb01deokhugamteam07.dto.notification.response.CursorPageResponseNotificationDto;
import com.part3.team07.sb01deokhugamteam07.service.NotificationService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
      @RequestParam UUID userId,
      @RequestParam(required = false, defaultValue = "desc") String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) String after,
      @RequestParam(required = false, defaultValue = "20") int limit
  ) {
    log.info("알림 조회 요청: userId={}, direction={}, cursor={}, after={}, limit={}",
        userId, direction, cursor, after, limit);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(notificationService.find(userId, direction, cursor, after, limit));
  }
}
