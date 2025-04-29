package com.part3.team07.sb01deokhugamteam07.controller;

import com.part3.team07.sb01deokhugamteam07.dto.notification.NotificationDto;
import com.part3.team07.sb01deokhugamteam07.dto.notification.request.NotificationUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.notification.response.CursorPageResponseNotificationDto;
import com.part3.team07.sb01deokhugamteam07.service.NotificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

  @PatchMapping("/{notificationId}")
  public ResponseEntity<NotificationDto> update(
      @PathVariable @NotNull UUID notificationId,
      @RequestHeader("Deokhugam-Request-User-ID") @NotNull UUID userId,
      @RequestBody @Valid NotificationUpdateRequest request
  ) {
    log.info("알림 상태 수정 요청: notificationId={}, userId={}, request={}", notificationId, userId,
        request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(notificationService.update(notificationId, userId, request));
  }

  @PatchMapping("/read-all")
  public ResponseEntity<Void> updateAll(
      @RequestHeader("Deokhugam-Request-User-ID") @NotNull UUID userId
  ){
    log.info("모든 알림 상태 읽음 처리: userId={}", userId);
    notificationService.updateAll(userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  /**
   * 알림 목록을 커서 기반 페이지네이션으로 조회합니다.
   **/
  @GetMapping
  public ResponseEntity<CursorPageResponseNotificationDto> find(
      @RequestParam @NotNull(message = "사용자 id는 필수입니다.") UUID userId,
      @RequestParam(required = false, defaultValue = "desc") @Pattern(regexp = "(?i)ASC|DESC", message = "direction은 ASC 또는 DESC만 가능합니다.") String direction,
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
