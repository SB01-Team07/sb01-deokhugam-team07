package com.part3.team07.sb01deokhugamteam07.controller;

import com.part3.team07.sb01deokhugamteam07.dto.comment.CommentDto;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentCreateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.comment.request.CommentUpdateRequest;
import com.part3.team07.sb01deokhugamteam07.dto.comment.response.CursorPageResponseCommentDto;
import com.part3.team07.sb01deokhugamteam07.service.CommentService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  public ResponseEntity<CommentDto> create(
      @RequestBody @Valid CommentCreateRequest createRequest
  ) {
    log.info("create comment request = {}", createRequest);
    CommentDto createdComment = commentService.create(createRequest);
    log.debug("create comment response = {}", createdComment);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdComment);
  }

  @PatchMapping(value = "/{commentId}")
  public ResponseEntity<CommentDto> update(
      @PathVariable UUID commentId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId,
      @RequestBody @Valid CommentUpdateRequest updateRequest
  ) {
    log.info("update comment request = {}", updateRequest);
    CommentDto updatedComment = commentService.update(commentId, userId, updateRequest);
    log.debug("update comment response = {}", updatedComment);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedComment);
  }

  @GetMapping(value = "/{commentId}")
  public ResponseEntity<CommentDto> find(
      @PathVariable UUID commentId
  ) {
    log.info("find comment request: commentId = {}", commentId);
    CommentDto findComment = commentService.find(commentId);
    log.debug("find comment response = {}", findComment);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(findComment);
  }

  @DeleteMapping(value = "/{commentId}")
  public ResponseEntity<Void> softDelete(
      @PathVariable UUID commentId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId
  ) {
    log.info("soft delete comment request: commentId = {}, userId = {}", commentId, userId);
    commentService.softDelete(commentId, userId);
    log.debug("soft delete comment success");
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @DeleteMapping(value = "/{commentId}/hard")
  public ResponseEntity<Void> hardDelete(
      @PathVariable UUID commentId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId
  ) {
    log.info("hard delete comment request: commentId = {}, userId = {}", commentId, userId);
    commentService.hardDelete(commentId, userId);
    log.debug("hard delete comment success");
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseCommentDto> findCommentsByReviewId(
      @RequestParam UUID reviewId,
      @RequestParam(required = false, defaultValue = "DESC") String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) LocalDateTime after,
      @RequestParam(defaultValue = "50") int limit
  ) {
    CursorPageResponseCommentDto response = commentService.findCommentsByReviewId(
        reviewId, direction, cursor, after, limit
    );
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }
}
